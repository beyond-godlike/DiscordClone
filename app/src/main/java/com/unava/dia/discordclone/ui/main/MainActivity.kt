package com.unava.dia.discordclone.ui.main

import android.content.SharedPreferences
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.navigation.NavigationView
import com.unava.dia.discordclone.R
import com.unava.dia.discordclone.data.User
import com.unava.dia.discordclone.other.Constants.KEY_FIRST_TIME_TOGGLE
import com.unava.dia.discordclone.other.addFragment
import com.unava.dia.discordclone.other.replaceFragment
import com.unava.dia.discordclone.ui.fragments.AudioCallFragment
import com.unava.dia.discordclone.ui.fragments.ChatFragment
import com.unava.dia.discordclone.ui.fragments.LoginFragment
import com.unava.dia.discordclone.ui.fragments.RegisterFragment
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.activity_main.*
import javax.inject.Inject


@AndroidEntryPoint
class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener,
    LoginFragment.LoginInteractionListener {

    @Inject
    lateinit var sharedPref: SharedPreferences

    @set:Inject
    var isFirstTimeOpen = true

    private lateinit var viewModel: MainViewModel

    lateinit var toggle: ActionBarDrawerToggle

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProvider(this).get(MainViewModel::class.java)
        setContentView(R.layout.activity_main)

        toggle = ActionBarDrawerToggle(this, drawer, R.string.open, R.string.close)
        drawer.addDrawerListener(toggle)
        toggle.syncState()

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        if (!isFirstTimeOpen) {
            val success = writePersonalDataToSharedPref()
            if (success) addFragment(R.id.fragmentContainer, RegisterFragment())
        } else {
            // if logged in
            addFragment(R.id.fragmentContainer, ChatFragment())
        }

        viewModel.loadUsers()
        observeViewModel()
    }

    private fun observeViewModel() {
        this.viewModel.users.observe(this, Observer {
            initAdapter(it)
        })

        this.viewModel.userId.observe(this, Observer {
            this.viewModel.loadChat(it)
       })
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.app_bar_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.miSettings -> {
                replaceFragment(R.id.fragmentContainer, LoginFragment())
            }
            R.id.miCall -> {
                replaceFragment(R.id.fragmentContainer, AudioCallFragment())
            }
            R.id.miVideo -> {
            }
            R.id.miClose -> finish()
        }

        // for drawer
        return toggle.onOptionsItemSelected(item)
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        drawer.closeDrawer(GravityCompat.START)
        return true
    }

    private fun writePersonalDataToSharedPref(): Boolean {
        sharedPref.edit()
            .putBoolean(KEY_FIRST_TIME_TOGGLE, false)
            .apply()
        return true
    }

    override fun onLoginClicked() {
        replaceFragment(R.id.fragmentContainer, ChatFragment())
    }

    private fun initAdapter(users: List<User>) {
        val adapter = UsersAdapter(users)
        rvChooseUserDrawer.adapter = adapter
        rvChooseUserDrawer.layoutManager =
            LinearLayoutManager(applicationContext, RecyclerView.VERTICAL, false)

        adapter.onItemClick = {
            this.viewModel.changeCurrentUserId(it)
        }
    }

    override fun onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }
}