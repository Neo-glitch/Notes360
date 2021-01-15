package com.neo.notes360.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.sqlite.db.SupportSQLiteDatabase
import java.util.*
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import kotlin.time.days


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
                                "note_database"
                            )
                                .addCallback(object : Callback() {
                                    override fun onOpen(db: SupportSQLiteDatabase) {
                                        super.onOpen(db)
                                        //            new PopulateDbAsync(INSTANCE).execute();
                                        val words = arrayOf(
                                            "dolphin",
                                            "crocodile",
                                            "cobra",
                                            "check",
                                            "check",
                                            "nigga",
                                            "boy"
                                        )

                                        // dummy
                                        var sExecutorService: ExecutorService? =
                                            Executors.newFixedThreadPool(4)
                                        sExecutorService?.execute(
                                            Runnable {
                                                val mDao: NoteDao
                                                mDao = noteRoomDatabase?.noteDao()!!
                                                mDao.deleteAllNotes()
                                                for (i in 0..words.size - 1) {
                                                    val word = Note(
                                                        i,
                                                        "title$i",
                                                        "content$2",
                                                        Calendar.getInstance().time
                                                    )
                                                    mDao.insert(word)
                                                }
                                            }
                                        )
                                    }
                                })
                                .build()
                    }
                }
            }
            return noteRoomDatabase
        }
    }


}

