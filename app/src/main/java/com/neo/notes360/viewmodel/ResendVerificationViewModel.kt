package com.neo.notes360.viewmodel

import android.app.Application
import android.widget.Toast
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth

class ResendVerificationViewModel(application: Application) : AndroidViewModel(application) {

    private val mContext = application
    private val mAuth = FirebaseAuth.getInstance()

    var progressBarVisibility: MutableLiveData<Int> = MutableLiveData()
    var closeResendVerificationFragment: MutableLiveData<Int> = MutableLiveData()


    /*
    temp auth user to resend verification email
     */
    fun authAndResendEmail(email: String, password: String) {
        closeResendVerificationFragment.value = 0

        progressBarVisibility.value = 1
        val authCredential =
            EmailAuthProvider.getCredential(email, password)  // get auth credential for signing in

        mAuth.signInWithCredential(authCredential).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                sendVerificationMail()
                mAuth.signOut()
            }
        }.addOnFailureListener {
            Toast.makeText(
                mContext,
                "Invalid email or password combination, check and try again",
                Toast.LENGTH_SHORT
            ).show()
            progressBarVisibility.value = 0
        }
    }

    private fun sendVerificationMail() {

        val user = mAuth.currentUser

        user?.sendEmailVerification()
            ?.addOnCompleteListener {task ->
                if(task.isSuccessful){
                    Toast.makeText(mContext, "Verification mail sent", Toast.LENGTH_SHORT).show()
                    progressBarVisibility.value = 0
                    closeResendVerificationFragment.value = 1
                } else {
                    Toast.makeText(mContext, "Couldn't send mail", Toast.LENGTH_SHORT).show()
                    progressBarVisibility.value = 0
                }
            }


    }
}
