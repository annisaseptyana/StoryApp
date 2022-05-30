package com.bangkit.storyapp

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
        const val PHOTO_URL = "photoUrl"
        const val NAME = "name"
        const val DESCRIPTION = "description"
    }
}