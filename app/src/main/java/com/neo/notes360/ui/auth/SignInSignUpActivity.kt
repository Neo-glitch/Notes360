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
        transaction.replace(R.id.container, signInFragment, getString(R.string.frag_signin))
        transaction.addToBackStack(getString(R.string.add_frag_signin))
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
        transaction.replace(R.id.container, signUpFragment, getString(R.string.frag_signup))
        transaction.addToBackStack(getString(R.string.add_frag_signup))
        transaction.commit()
    }

    override fun inflatePassWordResetFragment() {
        val passwordResetFragment= PasswordResetFragment()

        val transaction = manager.beginTransaction()
        transaction.setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out)
        transaction.replace(R.id.container, passwordResetFragment, getString(R.string.frag_password_reset))
        transaction.addToBackStack(getString(R.string.add_frag_password_reset))
        transaction.commit()
    }

    override fun inflateResendVerificationFragment() {
        val resendVerificationFragment = ResendVerificationFragment()

        val transaction = manager.beginTransaction()
        transaction.setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out)
        transaction.replace(R.id.container, resendVerificationFragment, getString(R.string.frag_resend_verif))
        transaction.addToBackStack(getString(R.string.add_frag_resend_verif))
        transaction.commit()
    }
}