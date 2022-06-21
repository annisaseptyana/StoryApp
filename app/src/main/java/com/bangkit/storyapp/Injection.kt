package com.bangkit.storyapp

import android.content.Context
import com.bangkit.storyapp.api.ApiConfig

object Injection {
    fun provideRepository(token: String, context: Context): StoryRepository {
        val apiService = ApiConfig.getApiService()
        return StoryRepository(token, apiService)
    }
}