package com.neo.notes360.repository

import android.app.Application
import android.os.Handler
import android.os.HandlerThread
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.paging.LivePagedListBuilder
import androidx.paging.PagedList
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.neo.notes360.Constants
import com.neo.notes360.dataSource.database.Note
import com.neo.notes360.dataSource.database.NoteDao
import com.neo.notes360.dataSource.database.NoteRoomDatabase
import com.neo.notes360.dataSource.firebase.FirebaseSource
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class noteRepository(application: Application) {
    companion object {
        private const val TAG = "Repository"
    }

    private val mFirebaseSource = FirebaseSource()
    private val mContext: Application
    private val noteDao: NoteDao
    val allNotes: LiveData<PagedList<Note>>
    private val mExecutorService: ExecutorService
    private lateinit var firebasedb: FirebaseFirestore
    val mAuth: FirebaseAuth by lazy {
        mFirebaseSource.getAuth()
    }
    private lateinit var currentUserId: String
    val mNoteUploadProgress: MutableLiveData<Int> = MutableLiveData()
    val mNoteDownloadProgress: MutableLiveData<Int> = MutableLiveData()
    private var mCancelFirebaseOperation: Boolean
    private var mHandlerThread: HandlerThread


    init {
        val noteDb = NoteRoomDatabase.getDatabase(application)
        noteDao = noteDb!!.noteDao()
        allNotes =
            LivePagedListBuilder(noteDao.notes, 10).build()    // retrieves LiveData list of notes
        mExecutorService =
            Executors.newFixedThreadPool(2)
        mContext = application
        mNoteUploadProgress.value = 0
        mNoteDownloadProgress.value = 0
        mCancelFirebaseOperation = false

        mHandlerThread = HandlerThread("download and insert note handler thread")
        mHandlerThread.start()
    }

    /*
    Things related to local database
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
        mCancelFirebaseOperation = false
        mNoteUploadProgress.value = 1
        currentUserId = mAuth.currentUser!!.uid
        val notesBackupList = mutableListOf<Note>()
        val noteIdList = mutableListOf<String>()
        if (!mCancelFirebaseOperation) {
            firebasedb = FirebaseFirestore.getInstance()
            firebasedb.collection(Constants.NOTES_360).document(Constants.NOTES)
                .collection(currentUserId)
                .orderBy(Constants.LAST_UPDATED, Query.Direction.ASCENDING)
                .get()
                .addOnCompleteListener {
                    if (it.isSuccessful) {
                        if (it.result!!.size() > 0) {
                            it.result!!.forEach { docSnapshot ->
                                val note: Note = docSnapshot.toObject(Note::class.java)
                                noteIdList.add(note.id.toString())
                                notesBackupList.add(note)
                            }
                            if (!mCancelFirebaseOperation) {
                                clearFirebaseDbForNewStart(noteIdList, notesBackupList)
                            } else {
                                displayToast("Note upload cancelled successfully")
                            }
                        } else {
                            if (!mCancelFirebaseOperation) {
                                uploadNotesToFirebase(
                                    allNotes.value!!.toMutableList(),
                                    notesBackupList
                                )
                            } else {
                                displayToast("Note upload cancelled successfully")
                            }
                        }
                    } else {
                        if (!mCancelFirebaseOperation) {
                            uploadNotesToFirebase(allNotes.value!!.toMutableList(), notesBackupList)
                        }
                    }
                }.addOnFailureListener {
                    mNoteUploadProgress.value = 0
                    displayToast("Error uploading notes, please check your internet")
                }
        } else {
            displayToast("Note upload cancelled successfully")
        }
    }

    // clears collection loc of current user in db before upload
    private fun clearFirebaseDbForNewStart(
        noteIdList: MutableList<String>,
        notesBackupList: MutableList<Note>
    ) {
        if (!mCancelFirebaseOperation) {
            for (i in noteIdList.indices) {
                if (!mCancelFirebaseOperation) {
                    val docRef = firebasedb.collection(Constants.NOTES_360)
                        .document(Constants.NOTES).collection(currentUserId)
                        .document(noteIdList[i])
                    docRef.delete()
                } else{
                    getAndPutPreviousCloudNotes(notesBackupList)
                    return
                }
            }
            if(!mCancelFirebaseOperation){
                uploadNotesToFirebase(allNotes.value!!.toMutableList(), notesBackupList)
            } else{
                getAndPutPreviousCloudNotes(notesBackupList)
            }
        } else {
            getAndPutPreviousCloudNotes(notesBackupList)
        }
    }

    private fun uploadNotesToFirebase(
        allNotes: MutableList<Note>,
        notesBackupList: MutableList<Note>
    ) {
        currentUserId = mAuth.currentUser!!.uid
        if (!mCancelFirebaseOperation) {
            for (note in allNotes) {
                var noteDocId = note.id
                if (!mCancelFirebaseOperation) {
                    val noteDocRef =
                        firebasedb.collection(Constants.NOTES_360).document(Constants.NOTES)
                            .collection(currentUserId).document(noteDocId.toString())
                    noteDocRef.set(note)
                } else {
                    getAndPutPreviousCloudNotes(notesBackupList)
                    return
                }
            }
            if (!mCancelFirebaseOperation) {
                mNoteUploadProgress.value = 0
                displayToast("Notes uploaded successfully")
            } else {
                getAndPutPreviousCloudNotes(notesBackupList)
                mNoteUploadProgress.value = 0
                displayToast("Error uploading notes, please check your internet")
            }
        } else { // note upload has been cancelled but, but prev note saved in cloud was delete, so put them back
            getAndPutPreviousCloudNotes(notesBackupList)
            mNoteUploadProgress.value = 0
        }
        mCancelFirebaseOperation = false
    }

    private fun getAndPutPreviousCloudNotes(notesBackupList: MutableList<Note>) {
        mNoteUploadProgress.value = 0
        displayToast("Note Upload cancelling, please wait...")
        firebasedb = FirebaseFirestore.getInstance()
        var toDeleteNoteIdList = mutableListOf<String>()

        firebasedb.collection(Constants.NOTES_360).document(Constants.NOTES)
            .collection(currentUserId).get()
            .addOnCompleteListener {
                if(it.isSuccessful){
                    if(it.result!!.size() > 0){
                        it.result!!.forEach {docSnapshot ->
                            val note: Note = docSnapshot.toObject(Note::class.java)
                            toDeleteNoteIdList.add(note.id!!.toString())
                        }
                        clearFirebaseForPreviousCloudNotes(toDeleteNoteIdList, notesBackupList)
                    } else{
                        putPreviousCloudNotes(notesBackupList)
                    }
                }
            }
    }

    private fun clearFirebaseForPreviousCloudNotes(
        toDeleteNoteIdList: MutableList<String>,
        notesBackupList: MutableList<Note>
    ) {
        firebasedb = FirebaseFirestore.getInstance()
        val batch = firebasedb.batch()
        for(i in toDeleteNoteIdList.indices){
            val docRef = firebasedb.collection(Constants.NOTES_360)
                .document(Constants.NOTES).collection(currentUserId)
                .document(toDeleteNoteIdList[i])
            batch.delete(docRef)
        }
        batch.commit()
        putPreviousCloudNotes(notesBackupList)
    }

    private fun putPreviousCloudNotes(notesBackupList: MutableList<Note>) {
        firebasedb = FirebaseFirestore.getInstance()
        notesBackupList.forEach { note ->
            var noteDocId = note.id
            val docRef = firebasedb.collection(Constants.NOTES_360)
                .document(Constants.NOTES).collection(currentUserId)
                .document(noteDocId.toString())
            docRef.set(note)
        }
        displayToast("Note upload cancelled successfully")
    }

    /////// Cloud related Stuff for Uploads ///////////


    /*
    Cloud related stuff for download
     */
    fun downloadNotesFromFirebase() {
        mNoteDownloadProgress.value = 1
        currentUserId = mAuth.currentUser!!.uid
        var notesList = mutableListOf<Note>()
        mCancelFirebaseOperation = false

        if (!mCancelFirebaseOperation) {
            firebasedb = FirebaseFirestore.getInstance()
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
                                // n.b: This if else block could have been could have been managed better, well later on will do that
                                if(!mCancelFirebaseOperation){
                                    Handler(mHandlerThread.looper).postDelayed(
                                        { insert(note) }, 80
                                    )
                                } else{
                                    mHandlerThread.quitSafely()
                                }
                            }
                            mNoteDownloadProgress.value = 0
                            displayToast("Notes downloaded successfully")
                        } else {
                            mNoteDownloadProgress.value = 0
                            displayToast("no note found in cloud, please ensure you have notes saved in the cloud")
                        }
                    } else {
                        mNoteDownloadProgress.value = 0
                        displayToast("Couldn't find any of your notes in the cloud, check your internet or ensure you have notes saved in the cloud")
                    }
                }.addOnFailureListener {
                    mNoteDownloadProgress.value = 0
                    displayToast("Error downloading notes, please check your internet")
                }
        }
        mCancelFirebaseOperation = false
    }

    fun signOut() {
        mAuth.signOut()
        displayToast("Signed out successfully")
    }

    private fun displayToast(message: String) {
        Toast.makeText(mContext, message, Toast.LENGTH_SHORT).show()
    }

    fun stopFirebaseOperation() {
        firebasedb.terminate()
        firebasedb.clearPersistence()
        mCancelFirebaseOperation = true
    }

}