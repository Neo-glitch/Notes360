package com.neo.notes360.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.neo.notes360.dataSource.database.Note
import com.neo.notes360.repository.noteRepository

class NoteDetailsActivityViewModel(application: Application) : AndroidViewModel(application) {
    var noteTitle: MutableLiveData<String> = MutableLiveData()
    var noteContent: MutableLiveData<String> = MutableLiveData()
    var noteId = 0
    private val noteRepository = noteRepository(application)

    fun insertNote(note: Note){
        noteRepository.insert(note)
    }

    fun updateNote(note: Note){
        noteRepository.update(note)
    }

}