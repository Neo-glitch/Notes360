package com.neo.notes360.dataSource.firebase

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore


// todo: implement this later in app for clean arch implementation
class FirebaseSource {

    private val mAuth by lazy {
        FirebaseAuth.getInstance()
    }

    private val mUser by lazy {
        mAuth.currentUser
    }

    private val mFirebaseDb: FirebaseFirestore by lazy {
        FirebaseFirestore.getInstance()
    }

    fun getFirebaseDb(): FirebaseFirestore = mFirebaseDb
    fun getAuth(): FirebaseAuth = mAuth



}