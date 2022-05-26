package com.neo.notes360.ui.addedit

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.widget.Toolbar
import com.neo.notes360.Constants
import com.neo.notes360.R
import kotlinx.android.synthetic.main.activity_add_edit.*
import kotlin.properties.Delegates

class AddEditActivity : AppCompatActivity() {
    private val mToolbar: Toolbar by lazy {
        findViewById<Toolbar>(R.id.toolbar_addedit)
    }

    private lateinit var noteTitle: String
    private lateinit var noteContent: String
    private var noteId by Delegates.notNull<Int>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_edit)
        setSupportActionBar(mToolbar)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_baseline_keyboard_backspace_24)

        val intentFromDetails = intent
        if(intentFromDetails.hasExtra(Constants.NOTE_ID)){  // note to be edited
            noteId = intentFromDetails.getIntExtra(Constants.NOTE_ID, -1)
            noteTitle = intentFromDetails.getStringExtra(Constants.NOTE_TITLE)!!
            noteContent = intentFromDetails.getStringExtra(Constants.NOTE_CONTENT)!!
        } else {        // new note
            noteTitle = ""
            noteContent = ""
            noteId = -1
        }

        addEditNoteTitle.setText(noteTitle)
        addEditNoteContent.setText(noteContent)
    }


    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.close_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                val resultIntent = Intent()
                resultIntent.putExtra(Constants.NOTE_ID, noteId)
                resultIntent.putExtra(Constants.NOTE_TITLE, addEditNoteTitle.text.toString())
                resultIntent.putExtra(Constants.NOTE_CONTENT, addEditNoteContent.text.toString())
                setResult(Activity.RESULT_OK, resultIntent)
                finish()
                return true
            }
            R.id.close -> {
                finish()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onBackPressed() {
        val resultIntent = Intent()
        resultIntent.putExtra(Constants.NOTE_ID, noteId)
        resultIntent.putExtra(Constants.NOTE_TITLE, addEditNoteTitle.text.toString())
        resultIntent.putExtra(Constants.NOTE_CONTENT, addEditNoteContent.text.toString())
        setResult(Activity.RESULT_OK, resultIntent)
        super.onBackPressed()
    }
}