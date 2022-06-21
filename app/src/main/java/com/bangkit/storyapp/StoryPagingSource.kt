package com.bangkit.storyapp

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.bangkit.storyapp.api.ApiService

class StoryPagingSource(
    private val token: String,
    private val apiService: ApiService): PagingSource<Int, ListStoryItem>(){

    override fun getRefreshKey(state: PagingState<Int, ListStoryItem>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            val anchorPage = state.closestPageToPosition(anchorPosition)
            anchorPage?.prevKey?.plus(1) ?: anchorPage?.nextKey?.minus(1)
        }
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, ListStoryItem> {
        return try {
            val position = params.key ?: INITIAL_PAGE_INDEX
            val bearerToken = HashMap<String, String>()
            bearerToken["Authorization"] = "Bearer $token"
            val responseData = apiService.getAllStories(token, position, params.loadSize)

            LoadResult.Page(
                data = responseData,
                prevKey = if (position == INITIAL_PAGE_INDEX) null else position -1,
                nextKey = if (responseData.isEmpty()) null else position +1
            )
        } catch (exception: Exception) {
            return LoadResult.Error(exception)
        }
    }

    companion object {
        const val INITIAL_PAGE_INDEX = 1
    }

}