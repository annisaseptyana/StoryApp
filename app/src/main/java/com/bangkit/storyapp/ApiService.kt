package com.bangkit.storyapp

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
        @Field("name") name: String,
        @Field("email") email: String,
        @Field("password") password: String
    ): Call<LoginResponse>
}