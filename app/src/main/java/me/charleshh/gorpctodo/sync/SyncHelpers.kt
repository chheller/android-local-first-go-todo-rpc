package me.charleshh.gorpctodo.sync


import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.work.Constraints
import androidx.work.ForegroundInfo
import androidx.work.NetworkType
import kotlinx.coroutines.CancellationException
import me.charleshh.gorpctodo.R
import kotlin.apply

const val SYNC_TOPIC = "sync"
private const val SYNC_NOTIFICATION_ID = 0
private const val SYNC_NOTIFICATION_CHANNEL_ID = "SyncNotificationChannel"

// All sync work needs an internet connectionS
val SyncConstraints
    get() = Constraints.Builder()
        .setRequiredNetworkType(NetworkType.CONNECTED)
        .build()

/**
 * Foreground information for sync on lower API levels when sync workers are being
 * run with a foreground service
 */
fun Context.syncForegroundInfo() = ForegroundInfo(
    SYNC_NOTIFICATION_ID,
    syncWorkNotification(),
)

/**
 * Notification displayed on lower API levels when sync workers are being
 * run with a foreground service
 */
private fun Context.syncWorkNotification(): Notification {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        val channel = NotificationChannel(
            SYNC_NOTIFICATION_CHANNEL_ID,
            getString(R.string.sync_work_notification_channel_name),
            NotificationManager.IMPORTANCE_DEFAULT,
        ).apply {
            description = getString(R.string.sync_work_notification_channel_description)
        }
        // Register the channel with the system
        val notificationManager: NotificationManager? =
            getSystemService(Context.NOTIFICATION_SERVICE) as? NotificationManager

        notificationManager?.createNotificationChannel(channel)
    }

    return NotificationCompat.Builder(
        this,
        SYNC_NOTIFICATION_CHANNEL_ID,
    )
        .setContentTitle(getString(R.string.sync_work_notification_title))
        .setPriority(NotificationCompat.PRIORITY_DEFAULT)
        .build()
}
/**
 * Interface marker for a class that manages synchronization between local data and a remote
 * source for a [Syncable].
 */
interface Synchronizer {
    suspend fun getChangeListVersions(): ChangeListVersions

    suspend fun updateChangeListVersions(update: ChangeListVersions.() -> ChangeListVersions)

    /**
     * Syntactic sugar to call [Syncable.syncWith] while omitting the synchronizer argument
     */
    suspend fun Syncable.sync() = this@sync.syncWith(this@Synchronizer)
}
/**
 * Interface marker for a class that is synchronized with a remote source. Syncing must not be
 * performed concurrently and it is the [Synchronizer]'s responsibility to ensure this.
 */
interface Syncable {
    /**
     * Synchronizes the local database backing the repository with the network.
     * Returns if the sync was successful or not.
     */
    suspend fun syncWith(synchronizer: Synchronizer): Boolean
}
/**
 * Attempts [block], returning a successful [Result] if it succeeds, otherwise a [Result.Failure]
 * taking care not to break structured concurrency
 */
private suspend fun <T> suspendRunCatching(block: suspend () -> T): Result<T> = try {
    Result.success(block())
} catch (cancellationException: CancellationException) {
    throw cancellationException
} catch (exception: Exception) {
    Log.i(
        "suspendRunCatching",
        "Failed to evaluate a suspendRunCatchingBlock. Returning failure Result",
        exception,
    )
    Result.failure(exception)
}

/**
 * Utility function for syncing a repository with the network.
 * [versionReader] Reads the current version of the model that needs to be synced
 * [changeListFetcher] Fetches the change list for the model
 * [versionUpdater] Updates the [ChangeListVersions] after a successful sync
 * [modelDeleter] Deletes models by consuming the ids of the models that have been deleted.
 * [modelUpdater] Updates models by consuming the ids of the models that have changed.
 *
 * Note that the blocks defined above are never run concurrently, and the [Synchronizer]
 * implementation must guarantee this.
 */
suspend fun Synchronizer.changeListSync(
    versionReader: (ChangeListVersions) -> Int,
    changeListFetcher: suspend (Int) -> List<NetworkChangeList>,
    versionUpdater: ChangeListVersions.(Int) -> ChangeListVersions,
    modelDeleter: suspend (List<String>) -> Unit,
    modelUpdater: suspend (List<String>) -> Unit,
) = suspendRunCatching {
    // Fetch the change list since last sync (akin to a git fetch)
    val currentVersion = versionReader(getChangeListVersions())
    val changeList = changeListFetcher(currentVersion)
    if (changeList.isEmpty()) return@suspendRunCatching true

    val (deleted, updated) = changeList.partition(NetworkChangeList::isDelete)

    // Delete models that have been deleted server-side
    modelDeleter(deleted.map(NetworkChangeList::id))

    // Using the change list, pull down and save the changes (akin to a git pull)
    modelUpdater(updated.map(NetworkChangeList::id))

    // Update the last synced version (akin to updating local git HEAD)
    val latestVersion = changeList.last().changeListVersion
    updateChangeListVersions {
        versionUpdater(latestVersion)
    }
}.isSuccess