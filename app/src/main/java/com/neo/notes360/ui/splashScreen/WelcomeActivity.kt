package com.neo.notes360.ui.splashScreen

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.HandlerThread
import android.view.View
import com.neo.notes360.R
import com.neo.notes360.ui.MainActivity

class WelcomeActivity : AppCompatActivity() {
    private val mWindow by lazy {
        window
    }

    private lateinit var mHandlerThread: HandlerThread


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_welcome)

        mHandlerThread = HandlerThread(getString(R.string.welcome_activity_handler_thread_name))
        mHandlerThread.start()
        Handler(mHandlerThread.looper).postDelayed({startMainActivity()}, 400)
        mWindow.decorView.systemUiVisibility = (
                View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                )
    }

    private fun startMainActivity() {
        startActivity(Intent(this, MainActivity::class.java))
        finish()
        mHandlerThread.quitSafely()
    }
}