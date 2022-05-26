package com.neo.notes360.ui.auth.passwordreset

import android.app.Application
import android.widget.Toast
import androidx.lifecycle.MutableLiveData
import com.google.firebase.auth.FirebaseAuth
import com.neo.notes360.dataSource.firebase.FirebaseSource

class PasswordResetRepository(application: Application) {
    private  val mContext = application
    private val mFirebaseSource = FirebaseSource()
    var mProgressBarVisibility: MutableLiveData<Int> = MutableLiveData()
    var mOnBackPressed: MutableLiveData<Int> = MutableLiveData()
    private val mAuth by lazy{
        mFirebaseSource.getAuth()
    }

    fun sendResetEmail(email: String){
        mProgressBarVisibility.value = 1
        mOnBackPressed.value = 0

        mAuth.sendPasswordResetEmail(email).addOnCompleteListener {task ->
            if(task.isSuccessful){
                displayToast("Password reset link sent to mail")
                mProgressBarVisibility.value = 0
                mOnBackPressed.value = 1
            } else{
                displayToast("Error in sending password reset mail, try again later")
                mProgressBarVisibility.value = 0
            }
        }.addOnFailureListener {
            mProgressBarVisibility.value = 0
            displayToast("Error in sending password reset mail, please check your internet")
        }
    }

    private fun displayToast(message: String) {
        Toast.makeText(mContext, message, Toast.LENGTH_SHORT).show()
    }
}