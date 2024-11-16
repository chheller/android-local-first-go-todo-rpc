package domain

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class TodoLocalEntity(
    @PrimaryKey val id: String,
    @ColumnInfo(name = "title") val title: String,
    @ColumnInfo(name = "done") val boolean: Boolean,
    @ColumnInfo(name = "created_at") val createdAt: Boolean,
    @ColumnInfo(name = "modified_at") val modifiedAt: Boolean
)