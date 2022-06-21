package com.bangkit.storyapp

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import java.lang.IllegalArgumentException

class StoryViewModel(storyRepository: StoryRepository): ViewModel() {

    val stories: LiveData<PagingData<ListStoryItem>> = storyRepository.getStory().cachedIn(viewModelScope)
}

class ViewModelFactory(private val token: String, private val context: Context): ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {

        if (modelClass.isAssignableFrom(StoryViewModel::class.java)) {

            @Suppress("UNCHECKED CAST")
            return StoryViewModel(Injection.provideRepository(token, context)) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}