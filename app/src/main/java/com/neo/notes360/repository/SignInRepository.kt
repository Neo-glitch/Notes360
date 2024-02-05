package com.neo.notes360.repository

import android.app.Application
import android.widget.Toast
import androidx.lifecycle.MutableLiveData
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.neo.notes360.dataSource.firebase.FirebaseSource

class SignInRepository (application: Application){
    private val mFirebaseSource = FirebaseSource()
    private val mContext = application
    var mShowProgressBar: MutableLiveData<Int> = MutableLiveData()  // 0 hide progress bar, 1 shows it
    var mOnBackPressed: MutableLiveData<Int> = MutableLiveData()
    private lateinit var mAuthStateListener: FirebaseAuth.AuthStateListener   // listens for changes to auth state of user

    val mAuth: FirebaseAuth by lazy {
        mFirebaseSource.getAuth()
    }

    init{
        mShowProgressBar.value = 0
        setupFirebaseAuth()
    }

    fun signInUser(email:String, password: String){
        mShowProgressBar.value = 1
        mAuth.signInWithEmailAndPassword(email, password).addOnSuccessListener {
            mShowProgressBar.value = 0
        }.addOnFailureListener {
        displayToast("Login failed, please check your internet or ensure login credentials are correct")
            mShowProgressBar.value = 0
        }
    }

    private fun displayToast(message: String) {
        Toast.makeText(mContext, message, Toast.LENGTH_SHORT).show()
    }

   private fun setupFirebaseAuth() {
        mAuthStateListener = object : FirebaseAuth.AuthStateListener {
            override fun onAuthStateChanged(firebaseAuth: FirebaseAuth) {
                val user = firebaseAuth.currentUser
                if (user != null) {
                    if (user.isEmailVerified) {
                        displayToast("Logged in as: ${user.displayName}")
                        mOnBackPressed.value = 1
                    } else {
                        displayToast("Email is not verified, please check your mail for verification link or resend code")
                        mAuth.signOut()
                    }
                } else {
                    // user is signedOut
                }
            }
        }
    }

    fun onStart(){
        mAuth.addAuthStateListener(mAuthStateListener)
    }

    fun onStop(){
        mAuth.removeAuthStateListener(mAuthStateListener)
    }
}