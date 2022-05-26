package com.neo.notes360.ui.auth.signinsignup.signin

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.observe
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
    private val mViewModel by lazy {
        ViewModelProvider(this)[SignInViewModel::class.java]
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
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

        showOrHideProgressBar()
        monitorOnBackPressed()

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
            hideKeyboard(it)
            if (email.text.toString().trim().isNotEmpty() && password.text.toString().trim()
                    .isNotEmpty()
            ) {
                signInUser(email.text.toString().trim(), password.text.toString().trim())
            } else {
                Toast.makeText(
                    context,
                    getString(R.string.ask_for_email_and_password),
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

        return view
    }

    private fun hideKeyboard(view: View) {
        val inputMethodManager =
            activity?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(view.windowToken, 0)
    }

    private fun showOrHideProgressBar() {
        mViewModel.mShowProgressBar.observe(viewLifecycleOwner, Observer {
            if (it == 1) {
                showProgressBar()
            } else {
                hideProgressBar()
            }
        })
    }

    private fun monitorOnBackPressed() {
        mViewModel.mOnBackPressed.observe(viewLifecycleOwner) {
            if (it == 1) {
                activity?.onBackPressed()
            }
        }
    }

    private fun signInUser(email: String, password: String) {
        mViewModel.signInUser(email, password)
    }

    override fun onStart() {
        super.onStart()
        mViewModel.onStart()
    }

    override fun onStop() {
        super.onStop()
        mViewModel.onStop()
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