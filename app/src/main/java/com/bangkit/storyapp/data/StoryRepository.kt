package com.bangkit.storyapp.data

import androidx.lifecycle.LiveData
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.liveData
import com.bangkit.storyapp.api.ApiService
import com.bangkit.storyapp.response.StoryListResponseItem

class StoryRepository(private val storyDatabase: StoryDatabase, private val apiService: ApiService) {

    fun getStory(token: String): LiveData<PagingData<StoryListResponseItem>> {

        return Pager(
            config = PagingConfig(pageSize = 3),
            pagingSourceFactory = { StoryPagingSource(token, apiService) }
        ).liveData
    }
}