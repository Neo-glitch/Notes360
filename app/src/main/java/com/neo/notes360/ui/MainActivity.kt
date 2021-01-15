package com.neo.notes360.ui

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
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
import com.neo.notes360.Constants
import com.neo.notes360.R
import com.neo.notes360.database.Note
import com.neo.notes360.model.Idelete
import com.neo.notes360.model.NoteRvAdapter
import com.neo.notes360.viewmodel.MainActivityViewModel

class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener, Idelete {
    // widgets
    private lateinit var mDrawerLayout: DrawerLayout
    private lateinit var mToggle: ActionBarDrawerToggle
    private lateinit var mNavigationView: NavigationView
    private lateinit var mToolbar: Toolbar


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

        mToolbar = findViewById(R.id.toolbar)
        setSupportActionBar(mToolbar)
        mDrawerLayout = findViewById(R.id.drawer)
        mNavigationView = findViewById(R.id.nav_view)


        initNavViewAndDrawer()
        initRecyclerView()

        mAddNoteFab.setOnClickListener {
            val intent = Intent(this, AddEditActivity::class.java)
            intent.putExtra(Constants.NEW_NOTE_INTENT, Constants.NEW_NOTE)
            startActivity(intent)
            overridePendingTransition(android.R.anim.slide_out_right, android.R.anim.slide_in_left)
        }
    }

    private fun initRecyclerView() {
        val adapter = NoteRvAdapter(this);
        mNotesRv.layoutManager = GridLayoutManager(this, 2)
        mNotesRv.adapter = adapter

        mViewModel.retrieveAllNotes().observe(this) { notes -> adapter.submitList(notes)}
    }

    private fun initNavViewAndDrawer() {
        mToolbar.navigationIcon = resources.getDrawable(R.drawable.ic_align_left, null)
        mToggle =
            ActionBarDrawerToggle(this, mDrawerLayout, mToolbar,
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


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
    }



    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        return when (item.itemId) {
            R.id.action_delete_notes -> true
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        mDrawerLayout.closeDrawer(GravityCompat.START)
        when(item.itemId){
            R.id.home -> {
                return true
            }
            R.id.signIn -> {
                val intent = Intent(this, SignInActivity::class.java)
                startActivity(intent)
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
                return true
            }
        }
        return true
    }

    override fun deleteSingleNote(note: Note) {
        TODO("Not yet implemented")
    }
}