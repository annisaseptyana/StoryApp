package com.bangkit.storyapp

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class StoryList(
    var name: String?,
    var description: String?,
    var photoUrl: String?,
) : Parcelable