package com.bangkit.storyapp

import android.content.Context
import com.bangkit.storyapp.api.ApiConfig
import com.bangkit.storyapp.data.StoryDatabase
import com.bangkit.storyapp.data.StoryRepository

object Injection {

    fun provideRepository(context: Context): StoryRepository {
        val database = StoryDatabase.getDatabase(context)
        val apiService = ApiConfig.getApiService()
        return StoryRepository(database, apiService)
    }
}