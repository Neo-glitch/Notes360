package com.neo.notes360.ui.auth

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import com.google.firebase.auth.FirebaseAuth
import com.neo.notes360.R
import com.neo.notes360.model.ISignInSignUpActivity


class SignInFragment : Fragment() {
    // widgets
    private lateinit var email: EditText
    private lateinit var password: EditText
    private lateinit var btnLogin: Button
    private lateinit var createAcct: TextView
    private lateinit var forgotPass: TextView
    private lateinit var resendCode: TextView
    private lateinit var mProgress: ProgressBar

    // var
    private lateinit var listener: ISignInSignUpActivity

    // firebase
    private lateinit var mAuthStateListener: FirebaseAuth.AuthStateListener   // listens for changes to auth state of user
    private lateinit var mAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setupFirebaseAuth()
        listener = activity as ISignInSignUpActivity
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_sign_in, container, false)

        email = view.findViewById(R.id.login_email_et)
        password = view.findViewById(R.id.login_pass_et)
        btnLogin = view.findViewById(R.id.btn_login)
        createAcct = view.findViewById(R.id.create_acct_tv)
        forgotPass = view.findViewById(R.id.forgot_pass_tv)
        resendCode = view.findViewById(R.id.resend_code_tv)
        mProgress = view.findViewById(R.id.signin_progress)

        mAuth = FirebaseAuth.getInstance()

        createAcct.setOnClickListener {
            listener.inflateSignUpFragment()
        }

        forgotPass.setOnClickListener {
            listener.inflatePassWordResetFragment()
        }

        resendCode.setOnClickListener {
            listener.inflateResendVerificationFragment()
        }

        btnLogin.setOnClickListener {
            if (email.text.toString().trim().isNotEmpty() && password.text.toString().trim()
                    .isNotEmpty()
            ) {
                signInUser(email.text.toString().trim(), password.text.toString().trim())
            } else {
                Toast.makeText(
                    context,
                    "email and password is need for login, please check",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

        return view
    }

    private fun signInUser(email: String, password: String) {
        showProgressBar()
        mAuth.signInWithEmailAndPassword(email, password).addOnSuccessListener {
            hideProgressBar()
        }.addOnFailureListener {
            Toast.makeText(context, "Login failed", Toast.LENGTH_SHORT).show()
            hideProgressBar()
        }
    }

    private fun setupFirebaseAuth() {
        mAuthStateListener = object : FirebaseAuth.AuthStateListener {
            override fun onAuthStateChanged(firebaseAuth: FirebaseAuth) {
                val user = firebaseAuth.currentUser
                if (user != null) {
                    if (user.isEmailVerified) {
                        Toast.makeText(
                            context,
                            "Logged in as: ${user.displayName}",
                            Toast.LENGTH_SHORT
                        ).show()
                        activity?.onBackPressed()
                    } else {
                        Toast.makeText(
                            context,
                            "Email is not verified, please check your mail for link or resend code",
                            Toast.LENGTH_SHORT
                        ).show()
                        mAuth.signOut()
                    }
                } else {
                    // user is signedOut
                }
            }
        }
    }

    override fun onStart() {
        super.onStart()
        mAuth.addAuthStateListener(mAuthStateListener)
    }

    override fun onStop() {
        super.onStop()
        if (mAuthStateListener != null) {
            mAuth.removeAuthStateListener(mAuthStateListener)
        }
    }

    private fun showProgressBar() {
        mProgress.visibility = View.VISIBLE
        btnLogin.visibility = View.INVISIBLE
    }

    private fun hideProgressBar() {
        if (mProgress.visibility == View.VISIBLE) {
            mProgress.visibility = View.GONE
            btnLogin.visibility = View.VISIBLE
        }
    }
}