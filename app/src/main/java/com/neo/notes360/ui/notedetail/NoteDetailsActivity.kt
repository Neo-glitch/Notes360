package com.neo.notes360.ui.notedetail

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.neo.notes360.Constants
import com.neo.notes360.R
import com.neo.notes360.dataSource.database.Note
import com.neo.notes360.ui.addedit.AddEditActivity
import kotlinx.android.synthetic.main.activity_note_details.*
import java.util.*

class NoteDetailsActivity : AppCompatActivity() {
    companion object{
        private const val TAG = "NoteDetailsActivity"
    }
    private lateinit var mViewModel: NoteDetailsActivityViewModel


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_note_details)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)


        mViewModel = ViewModelProvider(this)[NoteDetailsActivityViewModel::class.java]
        if(savedInstanceState == null){
            var intentFromHome = intent
            mViewModel.noteId = intentFromHome.getIntExtra(Constants.NOTE_ID, 0)
            mViewModel.noteTitle.value = intentFromHome.getStringExtra(Constants.NOTE_TITLE)!!
            mViewModel.noteContent.value = intentFromHome.getStringExtra(Constants.NOTE_CONTENT)!!
        }

        mViewModel.noteTitle.observe(this,
            Observer<String> { t -> supportActionBar?.title = t })
        mViewModel.noteContent.observe(this,
            Observer<String> { t -> noteDetailsContent.text = t })

        noteDetailsFab.setOnClickListener { v ->
            var editIntent = Intent(v.context, AddEditActivity::class.java)
            editIntent.putExtra(Constants.NOTE_TYPE, Constants.EDIT_NOTE)
            editIntent.putExtra(Constants.NOTE_ID, mViewModel.noteId)
            editIntent.putExtra(Constants.NOTE_TITLE, mViewModel.noteTitle.value)
            editIntent.putExtra(Constants.NOTE_CONTENT, mViewModel.noteContent.value)
            startActivityForResult(editIntent, Constants.EDIT_NOTE)
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            android.R.id.home -> {
                finish()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if(requestCode == Constants.EDIT_NOTE && resultCode == Activity.RESULT_OK){
            val noteId = data!!.getIntExtra(Constants.NOTE_ID, -1)
            val noteTitle = data.getStringExtra(Constants.NOTE_TITLE)
            val noteContent = data.getStringExtra(Constants.NOTE_CONTENT)
            val lastUpdated = Calendar.getInstance().time

            mViewModel.noteTitle.value = noteTitle
            mViewModel.noteContent.value = noteContent

            if(noteContent?.length!! < 1 || noteTitle?.length!! < 1){  // no note content and title
                Toast.makeText(this, getString(R.string.note_must_have_title_and_content), Toast.LENGTH_SHORT).show()
            } else {
                val note = Note(noteId, noteTitle, noteContent, lastUpdated)
                mViewModel.updateNote(note)
                Toast.makeText(this, getString(R.string.note_saved), Toast.LENGTH_SHORT).show()
            }
        }
    }

}