package com.neo.notes360.ui.splashScreen

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.HandlerThread
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.neo.notes360.R

class LaunchActivity : AppCompatActivity() {
    private val mWindow by lazy{
        window
    }

    private lateinit var mHandlerThread: HandlerThread

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mHandlerThread = HandlerThread(getString(R.string.launch_activity_thread_name))
        mHandlerThread.start()
        Handler(mHandlerThread.looper).post { run { startWelcomeActivity() } }
    }


    private fun startWelcomeActivity(){
        startActivity(Intent(this@LaunchActivity, WelcomeActivity::class.java))
        finish()
        mHandlerThread.quit()
    }
}