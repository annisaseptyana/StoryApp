package com.bangkit.storyapp.ui

import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.*
import androidx.activity.viewModels
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.ViewModelProvider
import com.bangkit.storyapp.databinding.ActivityMainBinding
import androidx.datastore.preferences.core.Preferences
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.bangkit.storyapp.*
import com.bangkit.storyapp.adapter.LoadingStateAdapter
import com.bangkit.storyapp.adapter.StoriesAdapter
import com.bangkit.storyapp.data.AppDataStore
import com.bangkit.storyapp.data.AuthViewModel
import com.bangkit.storyapp.data.ViewModelFactory
import kotlinx.coroutines.launch

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "login")

class MainActivity : AppCompatActivity() {

    private lateinit var binding : ActivityMainBinding
    private val mainViewModel: StoryViewModel by viewModels {
        ViewModelFactory(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if (applicationContext.resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            val layoutManager = GridLayoutManager (this, 2)
            binding.rvStorylist.layoutManager = layoutManager
        }
        else {
            val layoutManager = LinearLayoutManager(this)
            binding.rvStorylist.layoutManager = layoutManager
        }

        val pref = AppDataStore.getInstance(dataStore)
        val authViewModel = ViewModelProvider(this, ViewModelFactory(pref))[AuthViewModel::class.java]

        authViewModel.loginToken().observe(this) { token: String? ->

            if(token != null) {
                showLoading(true)
                val loginToken = "Bearer $token"
                getAllStories(loginToken)
                showLoading(false)
            }
            else {
                startActivity(Intent(this, LoginActivity::class.java))
                finish()
            }
        }
    }

    private fun getAllStories(loginToken: String) {

        val adapter = StoriesAdapter()
        binding.rvStorylist.adapter = adapter.withLoadStateFooter(
            footer = LoadingStateAdapter {
                adapter.retry()
            }
        )
        lifecycleScope.launch {
            mainViewModel.stories(loginToken).observe(this@MainActivity) {
                adapter.submitData(lifecycle, it)
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {

        val inflater = menuInflater
        inflater.inflate(R.menu.option_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        when (item.itemId) {

            R.id.create_story -> {
                val intent = Intent(this@MainActivity, AddStoryActivity::class.java)
                startActivity(intent)
                return true
            }

            R.id.view_maps -> {
                val intent = Intent(this@MainActivity, MapsActivity::class.java)
                startActivity(intent)
                return true
            }

            R.id.logout -> {
                val pref = AppDataStore.getInstance(dataStore)
                val authViewModel = ViewModelProvider(this@MainActivity, ViewModelFactory(pref))[AuthViewModel::class.java]
                authViewModel.clearToken()
                startActivity(Intent(this@MainActivity, LoginActivity::class.java))
                finish()
                return true
            }
            else -> return true
        }
    }

    companion object {
        const val TOKEN = "token"
    }

    private fun showLoading(isLoading: Boolean) {

        if (isLoading) {
            binding.progressBar.visibility = View.VISIBLE
        } else {
            binding.progressBar.visibility = View.GONE
        }
    }
}