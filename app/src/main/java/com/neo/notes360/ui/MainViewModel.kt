package com.neo.notes360.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.paging.PagedList
import com.google.firebase.auth.FirebaseAuth
import com.neo.notes360.dataSource.database.Note

class MainViewModel(application: Application) : AndroidViewModel(application) {
    private val allNotes: LiveData<PagedList<Note>>
    private val noteRepository = noteRepository(application)
    val mAuth: MutableLiveData<FirebaseAuth> = MutableLiveData()

    var mNoteUploadProgress: MutableLiveData<Int> = MutableLiveData()
    var mNoteDownloadProgress: MutableLiveData<Int> = MutableLiveData()

    init {
        allNotes = noteRepository.retrieveNotes()
        mAuth.value = noteRepository.mAuth
        mNoteUploadProgress = noteRepository.mNoteUploadProgress
        mNoteDownloadProgress = noteRepository.mNoteDownloadProgress
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

    fun signOut(){
        noteRepository.signOut()
    }

    fun stopFirebaseOperation() {
        noteRepository.stopFirebaseOperation()
    }

}