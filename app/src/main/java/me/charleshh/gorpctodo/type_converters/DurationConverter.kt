package me.charleshh.wearable_poc.database.type_converters

import androidx.room.TypeConverter
import java.time.Duration

class DurationConverter {
    @TypeConverter
    fun fromDuration(duration: Duration?): Long? {
        return duration?.toMillis()
    }

    @TypeConverter
    fun toDuration(millis: Long?): Duration? {
        return millis?.let { Duration.ofMillis(it) }
    }
}