package com.bangkit.storyapp.data

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class StoryList(
    var name: String?,
    var description: String?,
    var photoUrl: String?,
    var lat: String? = null,
    var lon: String? = null
) : Parcelable