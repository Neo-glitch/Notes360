package com.neo.notes360.dataSource.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.neo.notes360.Constants
import java.util.*


@Entity(tableName = Constants.TABLE_NAME)
data class Note (
    @PrimaryKey(autoGenerate = true)
    val id: Int?,
    @ColumnInfo(name = Constants.NOTE_TITLE_COLUMN)
    val noteTitle: String?,
    @ColumnInfo(name = Constants.NOTE_CONTENT_COLUMN)
    val noteContent: String?,
    @ColumnInfo(name = Constants.NOTE_LAST_UPDATED_COLUMN)
    val last_updated: Date?
){
     constructor(): this(null, "", "", null)
}