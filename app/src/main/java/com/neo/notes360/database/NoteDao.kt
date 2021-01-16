package com.neo.notes360.database

import androidx.lifecycle.LiveData
import androidx.paging.DataSource
import androidx.room.*


@Dao
interface NoteDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(note: Note)

    @get:Query("select * from notes_table order by last_updated desc")
    val notes: DataSource.Factory<Integer, Note>

    @Update
    fun update(note: Note)

    @Delete
    fun delete(note: Note)

    @Query("delete from notes_table")
    fun deleteAllNotes()
}