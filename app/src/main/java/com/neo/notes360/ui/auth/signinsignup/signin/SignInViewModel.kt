package com.neo.notes360.ui.auth.signinsignup.signin

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.google.firebase.auth.FirebaseAuth

class SignInViewModel(application: Application) : AndroidViewModel(application) {
    private val signInRepository = SignInRepository(application)
    var mShowProgressBar: MutableLiveData<Int> = signInRepository.mShowProgressBar
    var mOnBackPressed: MutableLiveData<Int> = signInRepository.mOnBackPressed
    val mAuth: FirebaseAuth by lazy {
        FirebaseAuth.getInstance()
    }

    fun signInUser(email:String, password: String){
        signInRepository.signInUser(email, password)
    }

    fun onStart(){
        signInRepository.onStart()
    }

    fun onStop(){
        signInRepository.onStop()
    }



}