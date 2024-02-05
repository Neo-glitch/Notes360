package com.neo.notes360.viewmodel

import android.app.Application
import android.widget.Toast
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.google.firebase.auth.FirebaseAuth
import com.neo.notes360.repository.PasswordResetRepository

class PasswordResetViewModel(application: Application) : AndroidViewModel(application) {

    private var passwordResetRepository = PasswordResetRepository(application)
    var mProgressBarVisibility = passwordResetRepository.mProgressBarVisibility
    var mOnBackPressed = passwordResetRepository.mOnBackPressed


    fun sendResetEmail(email: String){
        passwordResetRepository.sendResetEmail(email)
    }


}