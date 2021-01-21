package com.neo.notes360.ui

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
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
import com.neo.notes360.ui.auth.SignInSignUpActivity
import com.neo.notes360.viewmodel.MainActivityViewModel
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

    // firebase
    private lateinit var mAuth: FirebaseAuth


    private val mNotesRv by lazy {
        findViewById<RecyclerView>(R.id.notesRv)
    }
    private val mAddNoteFab by lazy {
        findViewById<FloatingActionButton>(R.id.addNoteFab)
    }

    private val mViewModel: MainActivityViewModel by lazy {
        ViewModelProvider(this)[MainActivityViewModel::class.java]
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        mToolbar = findViewById(R.id.toolbar_main)
        setSupportActionBar(mToolbar)
        mDrawerLayout = findViewById(R.id.drawer)

        mNavigationView = findViewById(R.id.nav_view)

        mAuth = FirebaseAuth.getInstance()


        initNavViewAndDrawer()
        initRecyclerView()

        mAddNoteFab.setOnClickListener {
            val intent = Intent(it.context, AddEditActivity::class.java)
            intent.putExtra(Constants.NOTE_TYPE, Constants.NEW_NOTE)
            startActivityForResult(intent, Constants.NEW_NOTE)
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
        }
    }


    private fun initRecyclerView() {
        val adapter = NoteRvAdapter(this);
        mNotesRv.layoutManager = GridLayoutManager( this, 2)
        mNotesRv.adapter = adapter

        mViewModel.retrieveAllNotes().observe(this) { notes ->
            adapter.submitList(notes)
            mNotesRv.post {mNotesRv.smoothScrollToPosition(0)}
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
        mNavigationView.setCheckedItem(R.id.home)
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == Constants.NEW_NOTE && resultCode == Activity.RESULT_OK) {
            val noteTitle = data!!.getStringExtra(Constants.NOTE_TITLE)?.trim()
            val noteContent = data.getStringExtra(Constants.NOTE_CONTENT)?.trim()
            val lastUpdated = Calendar.getInstance().time

            if (noteContent?.length!! < 1 || noteTitle?.length!! < 1) {  // no note content and title
                Toast.makeText(
                    this,
                    "Note must have a title and content to be saved",
                    Toast.LENGTH_SHORT
                ).show()
            } else {
                val note = Note(null, noteTitle, noteContent, lastUpdated)
                mViewModel.insertNote(note)
                Toast.makeText(this, "Note Saved", Toast.LENGTH_SHORT).show()
            }
        } else if (requestCode == Constants.EDIT_NOTE && resultCode == Activity.RESULT_OK) {
            val noteId = data!!.getIntExtra(Constants.NOTE_ID, -1)
            val noteTitle = data.getStringExtra(Constants.NOTE_TITLE)?.trim()
            val noteContent = data.getStringExtra(Constants.NOTE_CONTENT)?.trim()
            val lastUpdated = Calendar.getInstance().time

            if (noteContent?.length!! < 1 || noteTitle?.length!! < 1) {  // no note content and title
                Toast.makeText(
                    this,
                    "Note must have a title and content to be saved",
                    Toast.LENGTH_SHORT
                ).show()
            } else {
                val note = Note(noteId, noteTitle, noteContent, lastUpdated)
                mViewModel.updateNote(note)
                Toast.makeText(this, "Note Saved", Toast.LENGTH_SHORT).show()
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
                mAuth.signOut()
                return true
            }
        }
        return true
    }

    override fun onBackPressed() {
        if (mDrawerLayout.isDrawerOpen(GravityCompat.START)) {
            mDrawerLayout.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }

    override fun deleteSingleNote(note: Note) {
        mViewModel.deleteNote(note)
    }
}