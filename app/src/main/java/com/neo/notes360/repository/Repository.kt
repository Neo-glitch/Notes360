package com.neo.notes360.repository

import android.app.Application
import android.os.Handler
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.paging.LivePagedListBuilder
import androidx.paging.PagedList
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.WriteBatch
import com.neo.notes360.Constants
import com.neo.notes360.dataSource.database.Note
import com.neo.notes360.dataSource.database.NoteDao
import com.neo.notes360.dataSource.database.NoteRoomDatabase
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class Repository(application: Application) {
    companion object {
        private const val TAG = "Repository"
    }

    private val mContext: Application
    private val noteDao: NoteDao
    val allNotes: LiveData<PagedList<Note>>
    private val mExecutorService: ExecutorService

    private val firebasedb: FirebaseFirestore by lazy {
        FirebaseFirestore.getInstance()
    }
    private val mAuth: FirebaseAuth by lazy {
        FirebaseAuth.getInstance()
    }
    private lateinit var currentUserId: String
    private val noteIdList: MutableList<String>


    init {
        val noteDb = NoteRoomDatabase.getDatabase(application)
        noteDao = noteDb!!.noteDao()
        allNotes =
            LivePagedListBuilder(noteDao.notes, 10).build()    // retrieves LiveData list of notes
        mExecutorService =
            Executors.newFixedThreadPool(2)
        mContext = application
        noteIdList = mutableListOf<String>()
    }

    /*
    Things related to database
     */
    fun retrieveNotes(): LiveData<PagedList<Note>> = allNotes

    fun insert(note: Note) {
        mExecutorService.execute {
            noteDao.insert(note)
        }
    }

    fun update(note: Note) {
        mExecutorService.execute {
            noteDao.update(note)
        }
    }

    fun delete(note: Note) {
        mExecutorService.execute {
            noteDao.delete(note)
        }
    }

    fun deleteAllNotes() {
        mExecutorService.execute {
            noteDao.deleteAllNotes()
        }
    }
    //////////


    /*
    Cloud related stuff for uploads
     */
    fun queryFirebaseDbAndUpload() {
        currentUserId = mAuth.currentUser!!.uid
        firebasedb.collection(Constants.NOTES_360).document(Constants.NOTES)
            .collection(currentUserId)
            .orderBy(Constants.LAST_UPDATED, Query.Direction.DESCENDING)
            .get()
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    if (it.result!!.size() > 0) {
                        it.result!!.forEach { docSnapshot ->
                            val note: Note = docSnapshot.toObject(Note::class.java)
                            noteIdList.add(note.id.toString())
                        }
                        clearFirebaseDbForNewStart(noteIdList)
                    } else {
                        uploadNotesToFirebase(allNotes.value!!.toMutableList())
                    }
                } else {
                    uploadNotesToFirebase(allNotes.value!!.toMutableList())
                }
            }
    }

    // clears collection loc of current user in db before upload
    fun clearFirebaseDbForNewStart(noteIdList: MutableList<String>) {
        val batch: WriteBatch = firebasedb.batch()

        for (i in noteIdList.indices) {
            val docRef = firebasedb.collection(Constants.NOTES_360)
                .document(Constants.NOTES).collection(currentUserId)
                .document(noteIdList[i])
            batch.delete(docRef)
        }

        batch.commit().addOnCompleteListener { task ->
            if (task.isSuccessful) {
                uploadNotesToFirebase(allNotes.value!!.toMutableList())
            }
        }
    }

    fun uploadNotesToFirebase(allNotes: MutableList<Note>) {
        currentUserId = mAuth.currentUser!!.uid
        for (note in allNotes) {
            Log.d(TAG, "note Title: ${note.noteTitle}")
            var noteDocId = note.id

            val noteDocRef = firebasedb.collection(Constants.NOTES_360).document(Constants.NOTES)
                .collection(currentUserId).document(noteDocId.toString())
            noteDocRef.set(note).addOnSuccessListener {
                Log.d(TAG, "uploadNotesToFirebase: done")
            }.addOnFailureListener { e ->
            }
        }
    }

    fun downloadNotesFromFirebase() {
        currentUserId = mAuth.currentUser!!.uid
        var notesList = mutableListOf<Note>()

        firebasedb.collection(Constants.NOTES_360).document(Constants.NOTES)
            .collection(currentUserId)
            .get()
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    if (it.result!!.size() > 0) {
                        it.result!!.forEach { docSnapshot ->
                            val note: Note = docSnapshot.toObject(Note::class.java)
                            notesList.add(note)
                        }
                        deleteAllNotes()
                        for (note in notesList) {
                            Handler().postDelayed(
                                { insert(note) }, 80)
                        }
                        Log.d(TAG, "note download done")
                    } else {
                        Toast.makeText(
                            mContext,
                            "Couldn't find any of your notes in the cloud",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }
    }
    ///////

}