package com.neo.notes360.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters


@Database(entities = [Note::class], version = 1, exportSchema = false)
@TypeConverters(DateTypeConverter::class)
abstract class NoteRoomDatabase : RoomDatabase() {

    // dao
    abstract fun noteDao(): NoteDao


    companion object {
        private var noteRoomDatabase: NoteRoomDatabase? = null

        fun getDatabase(context: Context): NoteRoomDatabase?{
            if (noteRoomDatabase == null) {
                synchronized(NoteRoomDatabase::class.java) {
                    if (noteRoomDatabase == null) {
                        noteRoomDatabase =
                            Room.databaseBuilder<NoteRoomDatabase>(
                                context.applicationContext,
                                NoteRoomDatabase::class.java,
                                "note_database"
                            ).build()
                    }
                }
            }
            return  noteRoomDatabase
        }
    }
}

