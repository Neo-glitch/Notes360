package com.neo.notes360.ui

import android.app.Activity
import android.app.ProgressDialog
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.observe
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import com.neo.notes360.Constants
import com.neo.notes360.R
import com.neo.notes360.dataSource.database.Note
import com.neo.notes360.model.Idelete
import com.neo.notes360.model.NoteRvAdapter
import com.neo.notes360.ui.addedit.AddEditActivity
import com.neo.notes360.ui.auth.signinsignup.SignInSignUpActivity
import java.util.*

class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener, Idelete {

    companion object {
        private const val TAG = "MainActivity"
    }

    // widgets
    private lateinit var mDrawerLayout: DrawerLayout
    private lateinit var mToggle: ActionBarDrawerToggle
    private lateinit var mNavigationView: NavigationView
    private lateinit var mToolbar: Toolbar
    private lateinit var mNoNoteTv: TextView
    private lateinit var mNoNoteIv: ImageView
    private lateinit var downloadProgress: ProgressDialog
    private lateinit var uploadProgress: ProgressDialog

    // firebase
    private lateinit var mAuth: FirebaseAuth
    private val mNotesRv by lazy {
        findViewById<RecyclerView>(R.id.notesRv)
    }
    private val mAddNoteFab by lazy {
        findViewById<FloatingActionButton>(R.id.addNoteFab)
    }

    private val mViewModel: MainViewModel by lazy {
        ViewModelProvider(this)[MainViewModel::class.java]
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        mToolbar = findViewById(R.id.toolbar_main)
        setSupportActionBar(mToolbar)
        mDrawerLayout = findViewById(R.id.drawer)
        mNavigationView = findViewById(R.id.nav_view)
        mNoNoteTv = findViewById(R.id.no_note_tv)
        mNoNoteIv = findViewById(R.id.no_note_iv)

        mViewModel.mAuth.observe(this) {
            mAuth = it
        }

        initProgressDialog()
        initNavViewAndDrawer()
        initRecyclerView()

        mAddNoteFab.setOnClickListener {
            val intent = Intent(it.context, AddEditActivity::class.java)
            intent.putExtra(Constants.NOTE_TYPE, Constants.NEW_NOTE)
            startActivityForResult(intent, Constants.NEW_NOTE)
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
        }
    }

    private fun initProgressDialog() {
        initUploadProgress()
        initDownloadProgress()
    }

    private fun initDownloadProgress() {
        downloadProgress = ProgressDialog(this)
        mViewModel.mNoteDownloadProgress.observe(this) { progressVisibility ->
            if (progressVisibility == 1) {
                downloadProgress.setMessage(getString(R.string.download_progressbar_msg))
                downloadProgress.setCanceledOnTouchOutside(false)
                downloadProgress.show()
                downloadProgress.setOnCancelListener {
                    mViewModel.stopFirebaseOperation()
                }
            } else if (progressVisibility == 0) {
                downloadProgress.dismiss()
            }
        }
    }

    private fun initUploadProgress() {
        uploadProgress = ProgressDialog(this)
        mViewModel.mNoteUploadProgress.observe(this) { progressVisibility ->
            if (progressVisibility == 1) {
                uploadProgress.setMessage(getString(R.string.upload_progress_bar_msg))
                uploadProgress.setCanceledOnTouchOutside(false)
                uploadProgress.show()
                uploadProgress.setOnCancelListener {
                    mViewModel.stopFirebaseOperation()
                }
            } else if (progressVisibility == 0) {
                uploadProgress.dismiss()
            }
        }
    }

    private fun updateNavView() {
        val navView = mNavigationView.getHeaderView(0)  // 0 since just one header for navView
        val navUserName = navView.findViewById<TextView>(R.id.navUserName)
        val navUserEmail = navView.findViewById<TextView>(R.id.navUserEmail)
        if (mAuth.currentUser != null) {
            navUserName.visibility = View.VISIBLE
            navUserEmail.visibility = View.VISIBLE

            val userName = mAuth.currentUser!!.displayName
            val userEmail = mAuth.currentUser!!.email
            navUserName.text = userName
            navUserEmail.text = userEmail
        } else {
            navUserName.visibility = View.INVISIBLE
            navUserEmail.visibility = View.INVISIBLE
        }
    }


    private fun initRecyclerView() {
        val adapter = NoteRvAdapter(this);
        mNotesRv.layoutManager = GridLayoutManager(this, 2)
        mNotesRv.adapter = adapter

        mViewModel.retrieveAllNotes().observe(this) { notes ->
            if (notes.size == 0) {
                mNotesRv.visibility = View.INVISIBLE
                mNoNoteIv.visibility = View.VISIBLE
                mNoNoteTv.visibility = View.VISIBLE
            } else {
                mNotesRv.visibility = View.VISIBLE
                mNoNoteIv.visibility = View.GONE
                mNoNoteTv.visibility = View.GONE
                adapter.submitList(notes)
                mNotesRv.postDelayed(({ mNotesRv.smoothScrollToPosition(0) }), 300)
            }

        }
    }

    private fun initNavViewAndDrawer() {
        mToolbar.navigationIcon = resources.getDrawable(R.drawable.ic_align_left, null)
        mToggle =
            ActionBarDrawerToggle(
                this, mDrawerLayout, mToolbar,
                R.string.open,
                R.string.close
            )
        mToggle.drawerArrowDrawable.color = resources.getColor(android.R.color.white, null)
        mDrawerLayout.addDrawerListener(mToggle);

        mToggle.isDrawerIndicatorEnabled = true
        mToggle.syncState()

        mNavigationView.setNavigationItemSelectedListener(this)
        mNavigationView.setCheckedItem(R.id.home)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onResume() {
        super.onResume()
        updateNavView()
        mNavigationView.setCheckedItem(R.id.home)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == Constants.NEW_NOTE && resultCode == Activity.RESULT_OK) {
            val noteTitle = data!!.getStringExtra(Constants.NOTE_TITLE)?.trim()
            val noteContent = data.getStringExtra(Constants.NOTE_CONTENT)?.trim()
            val lastUpdated = Calendar.getInstance().time

            if (noteContent?.length!! < 1 || noteTitle?.length!! < 1) {  // no note content and title
                displayToast(getString(R.string.note_must_have_title_and_content))
            } else {
                val note = Note(null, noteTitle, noteContent, lastUpdated)
                mViewModel.insertNote(note)
                displayToast(getString(R.string.note_saved))
            }
        } else if (requestCode == Constants.EDIT_NOTE && resultCode == Activity.RESULT_OK) {
            val noteId = data!!.getIntExtra(Constants.NOTE_ID, -1)
            val noteTitle = data.getStringExtra(Constants.NOTE_TITLE)?.trim()
            val noteContent = data.getStringExtra(Constants.NOTE_CONTENT)?.trim()
            val lastUpdated = Calendar.getInstance().time

            if (noteContent?.length!! < 1 || noteTitle?.length!! < 1) {  // no note content and title
                displayToast(getString(R.string.note_must_have_title_and_content))
            } else {
                val note = Note(noteId, noteTitle, noteContent, lastUpdated)
                mViewModel.updateNote(note)
                displayToast(getString(R.string.note_saved))
            }
        }
    }


    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        return when (item.itemId) {
            R.id.action_delete_all_notes -> {
                mViewModel.deleteAllNotes()
                return true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        mDrawerLayout.closeDrawer(GravityCompat.START)
        when (item.itemId) {
            R.id.home -> {
                return true
            }
            R.id.signIn -> {
                val intent = Intent(this, SignInSignUpActivity::class.java)
                startActivity(intent)
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
                return true
            }
            R.id.signOut -> {
                mViewModel.signOut()
                updateNavView()
                return true
            }
            R.id.uploadNotes -> {
                if (checkIfNoteSaved()) {
                    if (mAuth.currentUser != null) {
                        displayNoteUploadAlert()
                    } else {
                        displayToast(getString(R.string.criteria_to_upload_note))
                    }
                } else {
                    displayToast(getString(R.string.must_have_note))
                }
                return true
            }
            R.id.downloadNotes -> {
                if (mAuth.currentUser != null) {
                    displayNoteDownloadAlert()
                } else {
                    displayToast(getString(R.string.criteria_to_download_note))
                }
                return true
            }
        }
        return true
    }

    private fun checkIfNoteSaved(): Boolean {
        var ifNoteSaved = false
        mViewModel.retrieveAllNotes().observe(this) { notes ->
            ifNoteSaved = notes.size > 0
        }
        return ifNoteSaved
    }

    private fun displayNoteUploadAlert() {
        val alertDialog = AlertDialog.Builder(this)
            .setTitle(getString(R.string.are_you_sure))
            .setMessage(getString(R.string.upload_dialog_message))
            .setPositiveButton(
                "Upload"
            ) { dialogInterface, i ->
                run {
                    mViewModel.queryFirebaseDbAndUpload()
                }
            }
            .setNegativeButton("Cancel") { dialogInterface, i -> }
        alertDialog.show()
    }

    private fun displayToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    private fun displayNoteDownloadAlert() {
        val alertDialog = AlertDialog.Builder(this)
            .setTitle(getString(R.string.are_you_sure))
            .setMessage(getString(R.string.download_dialog_message))
            .setPositiveButton(
                "Download"
            ) { dialogInterface, i ->
                run {
                    mViewModel.downloadNotesFromFirebase()
                }
            }
            .setNegativeButton("Cancel") { dialogInterface, i -> }
        alertDialog.show()
    }

    override fun onBackPressed() {
        if (mDrawerLayout.isDrawerOpen(GravityCompat.START)) {
            mDrawerLayout.closeDrawer(GravityCompat.START)
        } else if (!mDrawerLayout.isDrawerOpen(GravityCompat.START)) {
            super.onBackPressed()
        }
    }

    override fun deleteSingleNote(note: Note) {
        mViewModel.deleteNote(note)
    }
}