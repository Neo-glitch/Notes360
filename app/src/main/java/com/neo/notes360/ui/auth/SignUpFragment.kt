package com.neo.notes360.ui.auth

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.UserProfileChangeRequest
import com.neo.notes360.R


class SignUpFragment : Fragment() {
    private lateinit var email: EditText
    private lateinit var userName: EditText
    private lateinit var password: EditText
    private lateinit var confirmPassword: EditText
    private lateinit var btnSignUp: Button
    private lateinit var mProgress: ProgressBar

    // firebase
    private lateinit var mUser: FirebaseUser
    private lateinit var mAuth: FirebaseAuth


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
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

        mAuth = FirebaseAuth.getInstance()

        btnSignUp.setOnClickListener {
            if (email.text.toString().trim().isNotEmpty()
                && userName.text.toString().trim().isNotEmpty()
                && password.text.toString().trim().isNotEmpty()
                && confirmPassword.text.toString().trim().isNotEmpty()){

                if(checkPasswords(password.text.toString().trim(), confirmPassword.text.toString().trim())){
                    registerNewUser(email.text.toString().trim(), password.text.toString().trim())
                } else{
                    Toast.makeText(context, "Passwords do not match, check it please", Toast.LENGTH_SHORT).show()
                }
            } else{
                Toast.makeText(context, "To create an account all fields must be filled", Toast.LENGTH_SHORT).show()
            }
        }

        return view
    }

    private fun registerNewUser(email: String, password: String) {
        showProgressBar()

        mAuth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener {task ->
                if(task.isSuccessful){
                    mUser = mAuth.currentUser!!
                    val profileChangeReq = UserProfileChangeRequest.Builder()
                        .setDisplayName(userName.text.toString().trim())  // will be used as user's useName
                        .build()
                    mUser.updateProfile(profileChangeReq)
                    sendVerificationEmail()
                    mAuth.signOut()
                }
            }.addOnFailureListener{
                hideProgressBar()
                Toast.makeText(context, "Something went wrong, please check your internet and try again", Toast.LENGTH_SHORT).show()
            }

    }

    private fun sendVerificationEmail() {
        val user = mAuth.currentUser

        user?.sendEmailVerification()?.addOnCompleteListener {task ->
            if(task.isSuccessful){
                Toast.makeText(activity?.applicationContext, "check your mail for verification code", Toast.LENGTH_SHORT).show()
                activity?.onBackPressed()
            } else {
                Toast.makeText(activity?.applicationContext, "something went wrong and could not send your email", Toast.LENGTH_SHORT).show()
                activity?.onBackPressed()
            }
        }
    }

    companion object {

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