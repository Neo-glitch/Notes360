package com.neo.notes360.repository

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.neo.notes360.database.Note
import com.neo.notes360.database.NoteDao
import com.neo.notes360.database.NoteRoomDatabase
import kotlinx.coroutines.newFixedThreadPoolContext
import kotlinx.coroutines.newSingleThreadContext
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class Repository(application: Application) {
    private val noteDao: NoteDao
    val allNotes: LiveData<List<Note>>
    private val mExecutorService: ExecutorService

    init {
        val noteDb =NoteRoomDatabase.getDatabase(application)
        noteDao = noteDb!!.noteDao()
        allNotes = noteDao.notes    // retrieves LiveData list of notes
        mExecutorService =
            Executors.newFixedThreadPool(2)

    }

    /*
    Things related to database
     */
    fun insert(note: Note){
        mExecutorService.execute {
            noteDao.insert(note)
        }
    }

    fun update(note: Note){
        mExecutorService.execute {
            noteDao.update(note)
        }
    }

    fun delete(note: Note){
        mExecutorService.execute {
            noteDao.delete(note)
        }
    }

    fun deleteAllNotes(){
        noteDao.deleteAllNotes()
    }
    //////////


    /*
    Cloud related stuff
     */
    fun uploadNotesToFirebase(){

    }


    fun downloadNotesFromFirebase(): MutableLiveData<List<Note>>?{
        return null
    }
    ///////

}