package com.bangkit.storyapp.ui

import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.WindowInsets
import android.view.WindowManager
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.ViewModelProvider
import com.bangkit.storyapp.databinding.ActivityMainBinding
import androidx.datastore.preferences.core.Preferences
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.bangkit.storyapp.ListStoryItem
import com.bangkit.storyapp.R
import com.bangkit.storyapp.StoryListResponse
import com.bangkit.storyapp.api.ApiConfig
import com.bangkit.storyapp.datastore.AppDataStore
import com.bangkit.storyapp.datastore.AuthViewModel
import com.bangkit.storyapp.datastore.ViewModelFactory
import retrofit2.Call
import retrofit2.Response
import javax.security.auth.callback.Callback

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "login")

class MainActivity : AppCompatActivity() {

    private lateinit var binding : ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val pref = AppDataStore.getInstance(dataStore)
        val authViewModel = ViewModelProvider(this, ViewModelFactory(pref))[AuthViewModel::class.java]
        authViewModel.loginToken().observe(this) { token: String? ->
            getAllStories(token)
        }

        if (applicationContext.resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            val layoutManager = GridLayoutManager (this, 2)
            binding.rvStorylist.layoutManager = layoutManager
        }
        else {
            val layoutManager = LinearLayoutManager(this)
            binding.rvStorylist.layoutManager = layoutManager
        }
    }

    private fun getStoryList (listStory: List<ListStoryItem?>?) {
        val storyList = ArrayList<StoryList>()

        if (listStory != null) {
            for (item in listStory) {
                storyList.add (
                    StoryList(
                        item?.name,
                        item?.description,
                        item?.photoUrl
                    )
                )
            }
        }
        val adapter = StoryListAdapter(storyList)
        binding.rvStorylist.adapter = adapter

    }

    private fun getAllStories (token: String?) {
        val bearerToken = HashMap<String, String> ()
        bearerToken["Authorization"] = "Bearer $token"

        val client = ApiConfig.getApiService().getAllStories(bearerToken)
        client.enqueue(object : Callback, retrofit2.Callback<StoryListResponse> {
            override fun onResponse(
                call: Call<StoryListResponse>,
                response: Response<StoryListResponse>
            ) {
                if (response.isSuccessful) {
                    val responseBody = response.body()
                    if (responseBody != null) {
                        getStoryList(responseBody.listStory)
                    }
                }
                else {
                    Log.e(this@MainActivity.toString(), "onFailure: ${response.message()}")
                }
            }

            override fun onFailure(call: Call<StoryListResponse>, t: Throwable) {
                Log.e(this@MainActivity.toString(), "onFailure: ${t.message}")
            }

        })
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
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
            R.id.logout -> {
                val pref = AppDataStore.getInstance(dataStore)
                val authViewModel = ViewModelProvider(
                    this@MainActivity,
                    ViewModelFactory(pref)
                )[AuthViewModel::class.java]
                authViewModel.clearToken()
                startActivity(Intent(this@MainActivity, LoginActivity::class.java))
                finish()
                return true
            }
            else -> return true
        }
    }

    private fun setupView() {
        @Suppress("DEPRECATION")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.insetsController?.hide(WindowInsets.Type.statusBars())
        } else {
            window.setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN
            )
        }
        supportActionBar?.hide()
    }

    companion object {
        const val TOKEN = "token"
    }
}