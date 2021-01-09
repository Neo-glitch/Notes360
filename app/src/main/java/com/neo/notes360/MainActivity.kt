package com.neo.notes360

import android.content.Intent
import android.os.Bundle
import android.view.Gravity
import android.view.Menu
import android.view.MenuItem
import android.widget.TextView
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.navigation.NavigationView

class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {
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

    private var navViewSelection = R.id.home

    companion object{
        private const val NAV_VIEW_SELECTION = "nav_view_selection"
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        mToolbar = findViewById(R.id.toolbar)
        setSupportActionBar(mToolbar)

        mDrawerLayout = findViewById(R.id.drawer)
        mNavigationView = findViewById(R.id.nav_view)


        mToolbar.navigationIcon = resources.getDrawable(R.drawable.ic_align_left, null)
        mToggle = ActionBarDrawerToggle(this, mDrawerLayout, mToolbar, R.string.open, R.string.close)
        mToggle.drawerArrowDrawable.color = resources.getColor(android.R.color.white, null)
        mDrawerLayout.addDrawerListener(mToggle);

        mToggle.isDrawerIndicatorEnabled = true
        mToggle.syncState()

        mNavigationView.setNavigationItemSelectedListener(this)
        mNavigationView.setCheckedItem(R.id.home)

        mAddNoteFab.setOnClickListener {
            val intent = Intent(this, AddEditActivity::class.java)
            intent.putExtra(Constants.NEW_NOTE, Constants.NEW_NOTE)
            startActivity(intent)
            overridePendingTransition(android.R.anim.slide_out_right, android.R.anim.slide_in_left)
        }

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
                val intent = Intent(this,SignInActivity::class.java)
                startActivity(intent)
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
                return true
            }
        }
        return true
    }
}