package com.neo.notes360.ui.auth.signinsignup.signup

import android.app.Application
import android.widget.Toast
import androidx.lifecycle.MutableLiveData
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.UserProfileChangeRequest
import com.neo.notes360.dataSource.firebase.FirebaseSource

class SignUpRepository(application: Application) {
    private val mFirebaseSource = FirebaseSource()
    private val mContext = application
    var mShowProgressBar: MutableLiveData<Int> =
        MutableLiveData()  // 0 hide progress bar, 1 shows it
    var mOnBackPressed: MutableLiveData<Int> =
        MutableLiveData()  // 0 means remain in signup and, 1 means go to signin
    private val mAuth: FirebaseAuth by lazy {
        mFirebaseSource.getAuth()
    }

    init {
        mOnBackPressed.value = 0
        mShowProgressBar.value = 0
    }

    // Registration related code
    fun isEmailAlreadyRegistered(email: String, password: String, userName: String) {
        mOnBackPressed.value = 0
        mShowProgressBar.value = 1
        mAuth.fetchSignInMethodsForEmail(email).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                if (task.result?.signInMethods?.size!! == 0) {
                    registerNewUser(email, password, userName)
                } else {
                    mShowProgressBar.value = 0
                    displayToast("Email already registered, please try another email")
                }
            }
        }
    }

    private fun registerNewUser(email: String, password: String, userName: String) {
        mOnBackPressed.value = 0
        mShowProgressBar.value = 1
        mAuth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val user = mAuth.currentUser!!
                    val profileChangeReq = UserProfileChangeRequest.Builder()
                        .setDisplayName(userName)  // will be used as user's useName
                        .build()
                    user.updateProfile(profileChangeReq)
                    sendVerificationEmail(user)
                    mAuth.signOut()
                }
            }.addOnFailureListener {
                mShowProgressBar.value = 0
                displayToast("Something went wrong, please check your internet and try again")
            }
    }

    private fun sendVerificationEmail(user: FirebaseUser) {
        user.sendEmailVerification().addOnCompleteListener { task ->
            if (task.isSuccessful) {
                displayToast("check your mail for verification code")
                mOnBackPressed.value = 1
            } else {
                displayToast("something went wrong and could not send your email verification link")
                mShowProgressBar.value = 0
            }
        }
    }

    private fun displayToast(message: String) {
        Toast.makeText(mContext, message, Toast.LENGTH_SHORT).show()
    }
}