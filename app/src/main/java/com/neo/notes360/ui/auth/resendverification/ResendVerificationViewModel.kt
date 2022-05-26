package com.neo.notes360.ui.auth.resendverification

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData

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
