package com.neo.notes360.ui.auth

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.neo.notes360.R
import com.neo.notes360.viewmodel.PasswordResetViewModel


class PasswordResetFragment : Fragment() {
    private lateinit var etResetEmail : EditText
    private lateinit var btnResetPassword: Button
    private lateinit var mProgress: ProgressBar

    private val mPasswordResetViewModel by lazy {
        ViewModelProvider(this)[PasswordResetViewModel::class.java]
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_password_reset, container, false)

        etResetEmail = view.findViewById(R.id.password_reset_email_et)
        btnResetPassword = view.findViewById(R.id.btn_password_reset)
        mProgress = view.findViewById(R.id.resetpass_progress)

        btnResetPassword.setOnClickListener {
            if(etResetEmail.text.toString().trim().isNotEmpty()){
                sendResetEmail(etResetEmail.text.toString().trim())
            } else{
                Toast.makeText(context, getString(R.string.ask_for_registered_email), Toast.LENGTH_SHORT).show()
            }
        }

        initProgressBar()
        initOnBackPressed()
        return view
    }

    private fun initOnBackPressed() {
        mPasswordResetViewModel.mOnBackPressed.observe(viewLifecycleOwner, Observer { visibility ->
            if (visibility == 1) {
                (activity as SignInSignUpActivity).supportFragmentManager.popBackStack()
            }
        })
    }

    private fun initProgressBar() {
        mPasswordResetViewModel.mProgressBarVisibility.observe(
            viewLifecycleOwner,
            Observer { visibility ->
                if (visibility == 1) {
                    mProgress.visibility = View.VISIBLE
                    btnResetPassword.visibility = View.INVISIBLE
                } else {
                    mProgress.visibility = View.INVISIBLE
                    btnResetPassword.visibility = View.VISIBLE
                }
            })
    }

    private fun sendResetEmail(email: String) {
        mPasswordResetViewModel.sendResetEmail(email)
    }
}