package com.neo.notes360.ui.splashScreen

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.HandlerThread
import android.view.View
import androidx.appcompat.app.AppCompatActivity

class LaunchActivity : AppCompatActivity() {
    private val mWindow by lazy{
        window
    }

    private lateinit var mHandlerThread: HandlerThread

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

//        mWindow.decorView.systemUiVisibility = (
//                View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or View.SYSTEM_UI_FLAG_LAYOUT_STABLE
//                )

        mHandlerThread = HandlerThread("Launch Activity Thread")
        mHandlerThread.start()
        Handler(mHandlerThread.looper).post { run { startWelcomeActivity() } }


    }


    private fun startWelcomeActivity(){
        startActivity(Intent(this@LaunchActivity, WelcomeActivity::class.java))
        finish()
        mHandlerThread.quit()
    }
}