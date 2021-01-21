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
import com.neo.notes360.viewmodel.ResendVerificationViewModel


class ResendVerificationFragment : Fragment() {
    private lateinit var etResendEmail: EditText
    private lateinit var etResendPassword: EditText
    private lateinit var mProgress: ProgressBar
    private lateinit var btnResendVerification: Button

    private val mResendViewModel by lazy {
        ViewModelProvider(this)[ResendVerificationViewModel::class.java]
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_resend_verification, container, false)

        etResendEmail = view.findViewById(R.id.resend_email_et)
        etResendPassword = view.findViewById(R.id.resend_password_et)
        mProgress = view.findViewById(R.id.resend_progress)
        btnResendVerification = view.findViewById(R.id.btn_resend_mail)


        btnResendVerification.setOnClickListener {
            if(etResendEmail.text.toString().trim().isNotEmpty() and etResendPassword.text.toString().trim().isNotEmpty()){
                authAndResendEmail(etResendEmail.text.toString().trim(), etResendPassword.text.toString().trim())
            } else{
                Toast.makeText(context, "Please input your valid email and password", Toast.LENGTH_SHORT).show()
            }
        }

        mResendViewModel.progressBarVisibility.observe(viewLifecycleOwner, Observer {visibility ->
            if(visibility == 1){
                mProgress.visibility = View.VISIBLE
                btnResendVerification.visibility = View.INVISIBLE
            } else{
                mProgress.visibility = View.INVISIBLE
                btnResendVerification.visibility = View.VISIBLE
            }
        })

        mResendViewModel.closeResendVerificationFragment.observe(viewLifecycleOwner, Observer { visibility ->
            if(visibility == 1){
                (activity as SignInSignUpActivity).supportFragmentManager.popBackStack()
            }
        })

        return view
    }

    private fun authAndResendEmail(email:String, password: String) {
        mResendViewModel.authAndResendEmail(email, password)
    }
}