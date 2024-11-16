package me.charleshh.gorpctodo.sync

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import dagger.assisted.AssistedInject

/**
 * Syncs the data layer by delegating to the appropriate repository instances with
 * sync functionality.
 *
 * Based on NIA https://github.com/android/nowinandroid/blob/main/sync/work/src/main/kotlin/com/google/samples/apps/nowinandroid/sync/workers/SyncWorker.kt
 */
@HiltWorker
internal class SyncWorker @AssistedInject constructor(
    @Assisted private val appContext: Context,
    @Assisted workerParams: WorkerParameters,
    private val todoRepository,
    @Dispatcher(IO)
) : CoroutineWorker(appContext, workerParams), Synchronizer {

}