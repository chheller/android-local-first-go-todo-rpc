package me.charleshh.gorpctodo

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import domain.TodoLocalEntity
import me.charleshh.gorpctodo.domain.TodoLocalDao
import me.charleshh.wearable_poc.database.type_converters.DurationConverter
import me.charleshh.wearable_poc.database.type_converters.LocalDateConverter
import me.charleshh.wearable_poc.database.type_converters.LocalDateTimeConverter
import javax.inject.Singleton
import kotlin.jvm.java

@Database(
    entities = [TodoLocalEntity::class],
    version = 2,
    exportSchema = false,
)
@TypeConverters(LocalDateTimeConverter::class, LocalDateConverter::class, DurationConverter::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun localTodoDao(): TodoLocalDao
}

@Module
@InstallIn(SingletonComponent::class)
object AppDatabaseModule {
    @Provides
    @Singleton
    fun provideAppDatabase(@ApplicationContext appContext: Context): AppDatabase {
        return Room
            .databaseBuilder(appContext, AppDatabase::class.java, "application-database")
            // TODO: Remove this before production, so that we don't obliterate anyone's data
            .fallbackToDestructiveMigration()
            .build()
    }
}