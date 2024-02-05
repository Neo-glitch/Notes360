package com.neo.notes360.viewmodel

import android.app.Application
import android.widget.Toast
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.neo.notes360.repository.ResendVerificationRepository

class ResendVerificationViewModel(application: Application) : AndroidViewModel(application) {
    private val resendVerificationRepository = ResendVerificationRepository(application)

    var progressBarVisibility: MutableLiveData<Int> = resendVerificationRepository.progressBarVisibility
    var mOnBackPressed: MutableLiveData<Int> = resendVerificationRepository.mOnBackPressed


    /*
    temp auth user to resend verification email
     */
    fun authAndResendEmail(email: String, password: String) {
        resendVerificationRepository.authAndResendEmail(email, password)
    }
}
