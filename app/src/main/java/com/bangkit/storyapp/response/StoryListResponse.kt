package com.bangkit.storyapp.response

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName

data class StoryListResponse(

	@field:SerializedName("listStory")
	val listStory: List<StoryListResponseItem>,

	@field:SerializedName("error")
	val error: Boolean,

	@field:SerializedName("message")
	val message: String
)

@Entity(tableName = "story")
data class StoryListResponseItem (

	@field:SerializedName("photoUrl")
	val photoUrl: String,

	@field:SerializedName("createdAt")
	val createdAt: String,

	@field:SerializedName("name")
	val name: String,

	@field:SerializedName("description")
	val description: String,

	@field:SerializedName("lon")
	val lon: Double? = null,

	@PrimaryKey
	@field:SerializedName("id")
	val id: String,

	@field:SerializedName("lat")
	val lat: Double? = null
)