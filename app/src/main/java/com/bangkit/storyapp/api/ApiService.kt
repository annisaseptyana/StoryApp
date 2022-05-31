package com.bangkit.storyapp.api

import com.bangkit.storyapp.LoginResponse
import com.bangkit.storyapp.StoryListResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.*

interface ApiService {
    // Register
    @FormUrlEncoded
    @POST("register")
    fun register (
        @Field("name") name: String,
        @Field("email") email: String,
        @Field("password") password: String
    ): Call<RegisterResponse>

    // Login
    @FormUrlEncoded
    @POST("login")
    fun login (
        @Field("email") email: String,
        @Field("password") password: String
    ): Call<LoginResponse>

    // Story List
    @GET("stories")
    fun getAllStories(
        @HeaderMap token: Map<String, String>
    ): Call<StoryListResponse>

    // Add Story
    @Multipart
    @POST("stories")
    fun addNewStory(
        @Part file: MultipartBody.Part,
        @Part("description") description: RequestBody,
        @HeaderMap token: Map<String, String>
    ): Call<AddStoryResponse>
}