package com.bangkit.storyapp.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.bangkit.storyapp.databinding.ActivityDetailBinding
import com.bumptech.glide.Glide

class DetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDetailBinding

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        binding = ActivityDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.title = "Story"

        Glide.with(binding.imagePhoto)
            .load(intent.getStringExtra(PHOTO_URL))
            .into(binding.imagePhoto)

        binding.apply {
            textName.text = intent.getStringExtra(NAME)
            textDescription.text = intent.getStringExtra(DESCRIPTION)
        }
    }

    companion object {
        var PHOTO_URL: String? = "photoUrl"
        var NAME: String? = "name"
        var DESCRIPTION: String? = "description"
    }
}