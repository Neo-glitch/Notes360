package com.neo.notes360.dataSource.firebase

import com.google.firebase.auth.FirebaseAuth


// todo: implement this later in app for clean arch implementation
class FirebaseSource {

    private val mAuth by lazy {
        FirebaseAuth.getInstance()
    }

    private val mUser by lazy {
        mAuth.currentUser
    }


}