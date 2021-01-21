package com.neo.notes360.ui.auth

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.FragmentManager
import com.neo.notes360.R
import com.neo.notes360.model.ISignInSignUpActivity

class SignInSignUpActivity : AppCompatActivity(), ISignInSignUpActivity {
    private val manager: FragmentManager by lazy {
        supportFragmentManager
    }



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_in_sign_up)

        val signInFragment = SignInFragment()

        val transaction = manager.beginTransaction()
        transaction.replace(R.id.container, signInFragment, "FragSignIn")
        transaction.addToBackStack("AddFragSignIn")
        transaction.commit()
    }


    override fun onBackPressed() {
        if(manager.backStackEntryCount > 1){
            manager.popBackStack()
        } else {
            finish()
        }
    }

    override fun inflateSignUpFragment() {
        val signUpFragment = SignUpFragment()

        val transaction = manager.beginTransaction()
        transaction.setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out)
        transaction.replace(R.id.container, signUpFragment, "FragSignUp")
        transaction.addToBackStack("AddFragSignUp")
        transaction.commit()
    }

    override fun inflatePassWordResetFragment() {
        val passwordResetFragment= PasswordResetFragment()

        val transaction = manager.beginTransaction()
        transaction.setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out)
        transaction.replace(R.id.container, passwordResetFragment, "FragPassWordReset")
        transaction.addToBackStack("AddFragPassWordReset")
        transaction.commit()
    }

    override fun inflateResendVerificationFragment() {
        val resendVerificationFragment = ResendVerificationFragment()

        val transaction = manager.beginTransaction()
        transaction.setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out)
        transaction.replace(R.id.container, resendVerificationFragment, "FragResendVerification")
        transaction.addToBackStack("AddFragResendVerification")
        transaction.commit()
    }
}