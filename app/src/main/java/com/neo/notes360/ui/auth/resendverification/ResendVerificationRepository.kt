package com.neo.notes360.ui.auth.resendverification

import android.app.Application
import android.widget.Toast
import androidx.lifecycle.MutableLiveData
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.neo.notes360.dataSource.firebase.FirebaseSource

class ResendVerificationRepository(application: Application) {
    private val mFirebaseSource = FirebaseSource()
    private val mContext = application
    private val mAuth by lazy {
        mFirebaseSource.getAuth()
    }

    var progressBarVisibility: MutableLiveData<Int> = MutableLiveData()
    var mOnBackPressed: MutableLiveData<Int> = MutableLiveData()

    /*
  temp auth user to resend verification email
   */
    fun authAndResendEmail(email: String, password: String) {
        mOnBackPressed.value = 0

        progressBarVisibility.value = 1
        val authCredential =
            EmailAuthProvider.getCredential(email, password)  // get auth credential for signing in

        mAuth.signInWithCredential(authCredential).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                sendVerificationMail()
//                mAuth.signOut()
            }
        }.addOnFailureListener {
            displayToast("Invalid email or password combination, check and try again")
            progressBarVisibility.value = 0
        }
    }

    private fun sendVerificationMail() {
        val user = mAuth.currentUser

        user?.sendEmailVerification()
            ?.addOnCompleteListener {task ->
                if(task.isSuccessful){
                    displayToast("Verification mail sent")
                    progressBarVisibility.value = 0
                    mOnBackPressed.value = 1
                } else {
                    displayToast("Couldn't send verification mail, try again")
                    progressBarVisibility.value = 0
                }
            }
        mAuth.signOut()
    }

    private fun displayToast(message: String) {
        Toast.makeText(mContext, message, Toast.LENGTH_SHORT).show()
    }
}