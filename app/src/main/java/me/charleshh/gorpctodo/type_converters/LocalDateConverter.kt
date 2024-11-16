package me.charleshh.wearable_poc.database.type_converters

import androidx.room.TypeConverter
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneOffset
import kotlin.let

class LocalDateConverter {
    @TypeConverter
    fun fromTimestamp(value: Long?): LocalDate? {
        return value?.let { LocalDate.ofInstant(Instant.ofEpochMilli(it), ZoneOffset.UTC) }
    }

    @TypeConverter
    fun dateToTimestamp(date: LocalDate?): Long? {
        return date?.atStartOfDay()?.atZone(ZoneOffset.UTC)?.toInstant()?.toEpochMilli()
    }
}