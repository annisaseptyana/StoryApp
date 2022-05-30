package com.bangkit.storyapp

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
}