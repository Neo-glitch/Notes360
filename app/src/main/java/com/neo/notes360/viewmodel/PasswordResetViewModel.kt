package com.neo.notes360.viewmodel

import android.app.Application
import android.widget.Toast
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.google.firebase.auth.FirebaseAuth

class PasswordResetViewModel(application: Application) : AndroidViewModel(application) {
    private var mContext = application
    private var mAuth: FirebaseAuth = FirebaseAuth.getInstance()
    var progressBarVisibility: MutableLiveData<Int> = MutableLiveData()
    var closePasswordResetFragment: MutableLiveData<Int> = MutableLiveData()


    fun sendResetEmail(email: String){
        progressBarVisibility.value = 1
        closePasswordResetFragment.value = 0

        mAuth.sendPasswordResetEmail(email).addOnCompleteListener {task ->
            if(task.isSuccessful){
                Toast.makeText(mContext, "Password reset link sent to mail", Toast.LENGTH_SHORT).show()
                progressBarVisibility.value = 0
                closePasswordResetFragment.value = 1
            } else{
                Toast.makeText(mContext, "Error in sending password reset mail, try again later", Toast.LENGTH_SHORT).show()
                progressBarVisibility.value = 0
            }
        }
    }


}