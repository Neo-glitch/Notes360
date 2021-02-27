package com.neo.notes360.dataSource.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.sqlite.db.SupportSQLiteDatabase
import com.neo.notes360.Constants
import java.util.*
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors


@Database(entities = [Note::class], version = 1, exportSchema = false)
@TypeConverters(DateTypeConverter::class)
abstract class NoteRoomDatabase : RoomDatabase() {
    // dao
    abstract fun noteDao(): NoteDao

    companion object {
        private var noteRoomDatabase: NoteRoomDatabase? = null

        fun getDatabase(context: Context): NoteRoomDatabase? {
            if (noteRoomDatabase == null) {
                synchronized(NoteRoomDatabase::class.java) {
                    if (noteRoomDatabase == null) {
                        noteRoomDatabase =
                            Room.databaseBuilder<NoteRoomDatabase>(
                                context.applicationContext,
                                NoteRoomDatabase::class.java,
                                Constants.DATABASE_NAME
                            ).build()
                    }
                }
            }
            return noteRoomDatabase
        }
    }
}

