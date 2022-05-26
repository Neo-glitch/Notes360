package com.neo.notes360.ui.auth.passwordreset

import android.app.Application
import androidx.lifecycle.AndroidViewModel

class PasswordResetViewModel(application: Application) : AndroidViewModel(application) {

    private var passwordResetRepository = PasswordResetRepository(application)
    var mProgressBarVisibility = passwordResetRepository.mProgressBarVisibility
    var mOnBackPressed = passwordResetRepository.mOnBackPressed


    fun sendResetEmail(email: String){
        passwordResetRepository.sendResetEmail(email)
    }


}