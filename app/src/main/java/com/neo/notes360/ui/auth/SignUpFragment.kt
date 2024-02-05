package com.neo.notes360.ui.auth

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.neo.notes360.R
import com.neo.notes360.viewmodel.SignUpFragmentViewModel


class SignUpFragment : Fragment() {
    private lateinit var email: EditText
    private lateinit var userName: EditText
    private lateinit var password: EditText
    private lateinit var confirmPassword: EditText
    private lateinit var btnSignUp: Button
    private lateinit var mProgress: ProgressBar
    private val mSignUpViewModel by lazy{
        ViewModelProvider(this)[SignUpFragmentViewModel::class.java]
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_sign_up, container, false)
        email = view.findViewById(R.id.signup_email_et)
        userName = view.findViewById(R.id.signup_username_et)
        password = view.findViewById(R.id.signup_pass_et)
        confirmPassword = view.findViewById(R.id.signup_confirm_pass_et)
        btnSignUp = view.findViewById(R.id.btn_signup)
        mProgress = view.findViewById(R.id.signup_progress)


        btnSignUp.setOnClickListener {
            if (email.text.toString().trim().isNotEmpty()
                && userName.text.toString().trim().isNotEmpty()
                && password.text.toString().trim().isNotEmpty()
                && confirmPassword.text.toString().trim().isNotEmpty()){

                if(checkPasswords(password.text.toString().trim(), confirmPassword.text.toString().trim())){
                    hideKeyboard(it)
                    registerNewUser(email.text.toString().trim(), password.text.toString().trim(), userName.text.toString().trim())
                } else{
                    Toast.makeText(context, getString(R.string.passwords_not_match), Toast.LENGTH_SHORT).show()
                }
            } else{
                Toast.makeText(context, getString(R.string.fill_all_fields), Toast.LENGTH_SHORT).show()
            }
        }

        hideOrShowProgressBar()
        initOnBackPressed()
        return view
    }

    private fun hideKeyboard(view: View) {
        val inputMethodManager = activity?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(view.windowToken, 0)
    }


    private fun hideOrShowProgressBar() {
        mSignUpViewModel.mShowProgressBar.observe(viewLifecycleOwner, Observer{visibility ->
            if(visibility == 0){
                hideProgressBar()
            } else {
                showProgressBar()
            }
        })
    }

    private fun initOnBackPressed() {
        mSignUpViewModel.mOnBackPressed.observe(viewLifecycleOwner, Observer{goBack ->
            if(goBack == 1){
                activity?.onBackPressed()
            }
        })
    }

    private fun registerNewUser(email: String, password: String, userName: String) {
        mSignUpViewModel.isEmailAlreadyRegistered(email, password, userName)
    }

    private fun checkPasswords(s1: String, s2: String): Boolean {
        // checks if password and confirm password match
        return s1 == s2
    }

    private fun showProgressBar() {
        mProgress.visibility = View.VISIBLE
        btnSignUp.visibility = View.INVISIBLE
    }

    private fun hideProgressBar() {
        if (mProgress.visibility == View.VISIBLE) {
            mProgress.visibility = View.GONE
            btnSignUp.visibility = View.VISIBLE
        }
    }
}