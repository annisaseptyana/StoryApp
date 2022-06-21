package com.bangkit.storyapp.api

import com.bangkit.storyapp.ListStoryItem
import com.bangkit.storyapp.LoginResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.*

interface ApiService {
    @FormUrlEncoded
    @POST("register")
    fun register (
        @Field("name") name: String,
        @Field("email") email: String,
        @Field("password") password: String
    ): Call<RegisterResponse>

    @FormUrlEncoded
    @POST("login")
    fun login (
        @Field("email") email: String,
        @Field("password") password: String
    ): Call<LoginResponse>

    @GET("stories")
    suspend fun getAllStories(
        @Header("Authorization") Authorization: String,
        @Query("page") page: Int,
        @Query("size") size: Int
    ): List<ListStoryItem>

    @Multipart
    @POST("stories")
    fun addNewStory(
        @Part file: MultipartBody.Part,
        @Part("description") description: RequestBody,
        @HeaderMap token: Map<String, String>
    ): Call<AddStoryResponse>
}