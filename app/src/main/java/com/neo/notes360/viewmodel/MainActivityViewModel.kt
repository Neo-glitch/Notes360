package com.neo.notes360.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.paging.PagedList
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.neo.notes360.dataSource.database.Note
import com.neo.notes360.repository.Repository

class MainActivityViewModel(application: Application) : AndroidViewModel(application) {
    val allNotes: LiveData<PagedList<Note>>
    private val noteRepository = Repository(application)

    init {
        allNotes = noteRepository.retrieveNotes()
    }


    fun retrieveAllNotes(): LiveData<PagedList<Note>> = allNotes

    fun insertNote(note: Note) {
        noteRepository.insert(note)
    }

    fun updateNote(note: Note) {
        noteRepository.update(note)
    }

    fun deleteNote(note: Note) {
        noteRepository.delete(note)
    }

    fun deleteAllNotes() {
        noteRepository.deleteAllNotes()
    }


   /*
   firebase(cloud) related operations
    */
    fun queryFirebaseDbAndUpload() {
       noteRepository.queryFirebaseDbAndUpload()
    }


    fun downloadNotesFromFirebase(){
        noteRepository.downloadNotesFromFirebase()
    }

}