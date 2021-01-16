package com.neo.notes360.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.neo.notes360.database.Note
import com.neo.notes360.repository.Repository
import kotlin.properties.Delegates

class NoteDetailsActivityViewModel(application: Application) : AndroidViewModel(application) {
    var noteTitle: MutableLiveData<String> = MutableLiveData()
    var noteContent: MutableLiveData<String> = MutableLiveData()
    var noteId = 0
    private val noteRepository = Repository(application)

    fun insertNote(note: Note){
        noteRepository.insert(note)
    }

    fun updateNote(note: Note){
        noteRepository.update(note)
    }

}