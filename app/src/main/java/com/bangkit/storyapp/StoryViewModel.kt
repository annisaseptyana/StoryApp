package com.bangkit.storyapp

import android.content.Context
import androidx.lifecycle.*
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.bangkit.storyapp.data.StoryRepository
import com.bangkit.storyapp.response.StoryListResponseItem
import java.lang.IllegalArgumentException

class StoryViewModel(private val storyRepository: StoryRepository): ViewModel() {
    private val _stories = MutableLiveData<PagingData<StoryListResponseItem>>()

    fun stories(token: String): LiveData<PagingData<StoryListResponseItem>> {
        val response = storyRepository.getStory(token).cachedIn(viewModelScope)
        _stories.value = response.value
        return response
    }
}

class ViewModelFactory(private val context: Context): ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {

        if (modelClass.isAssignableFrom(StoryViewModel::class.java)) {

            @Suppress("UNCHECKED CAST")
            return StoryViewModel(Injection.provideRepository(context)) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}