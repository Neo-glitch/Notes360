package com.neo.notes360.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.neo.notes360.repository.SignUpRepository

class SignUpFragmentViewModel(application: Application) : AndroidViewModel(application) {
    private val signUpRepository = SignUpRepository(application)
    var mShowProgressBar: MutableLiveData<Int> = signUpRepository.mShowProgressBar
    var mOnBackPressed: MutableLiveData<Int> = signUpRepository.mOnBackPressed


    fun isEmailAlreadyRegistered(email: String, password: String, userName: String) {
        signUpRepository.isEmailAlreadyRegistered(email, password, userName)
    }

}