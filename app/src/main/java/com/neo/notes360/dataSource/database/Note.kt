package com.neo.notes360.dataSource.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey
import java.util.*


@Entity(tableName = "notes_table")
data class Note (
    @PrimaryKey(autoGenerate = true)
    val id: Int?,
    @ColumnInfo(name = "note_title")
    val noteTitle: String?,
    @ColumnInfo(name = "note_content")
    val noteContent: String?,
    @ColumnInfo(name = "last_updated")
    val last_updated: Date?
){
     constructor(): this(null, "", "", null)
}