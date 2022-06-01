package com.bangkit.storyapp.ui

import android.content.Context
import android.content.Intent
import android.content.Intent.ACTION_GET_CONTENT
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.FileProvider
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.ViewModelProvider
import com.bangkit.storyapp.*
import com.bangkit.storyapp.api.AddStoryResponse
import com.bangkit.storyapp.api.ApiConfig
import com.bangkit.storyapp.databinding.ActivityAddStoryBinding
import com.bangkit.storyapp.datastore.AppDataStore
import com.bangkit.storyapp.datastore.AuthViewModel
import com.bangkit.storyapp.datastore.ViewModelFactory
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "login")

class AddStoryActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAddStoryBinding
    private lateinit var currentPhotoPath: String
    private var attachFile: File? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddStoryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.title = "Create Story"

        binding.apply {
            buttonCamera.setOnClickListener {
               takePhoto()
            }
            buttonGallery.setOnClickListener {
                openGallery()
            }
            buttonUpload.setOnClickListener {
                uploadFile()
            }
        }
    }

    private val launcherIntentCamera = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
        if (it.resultCode == RESULT_OK) {
            val myFile = File(currentPhotoPath)
            val result = BitmapFactory.decodeFile(myFile.path)
            attachFile = myFile
            binding.previewImageView.setImageBitmap(result)
        }
    }

    private val launcherIntentGallery = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
        result ->
        if (result.resultCode == RESULT_OK) {
            val selectedImg: Uri = result.data?.data as Uri
            val myFile = uriToFile(selectedImg, this@AddStoryActivity)
            attachFile = myFile
            binding.previewImageView.setImageURI(selectedImg)
        }
    }

    private fun takePhoto() {
        val intentCamera = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        intentCamera.resolveActivity(packageManager)
        createCustomTempFile(application).also{
            val photoURI: Uri = FileProvider.getUriForFile(
                this@AddStoryActivity,
                "com.bangkit.storyapp",
                it
            )
            currentPhotoPath = it.absolutePath
            intentCamera.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
            launcherIntentCamera.launch(intentCamera)
        }
    }

    private fun openGallery() {
        val intent = Intent()
        intent.action = ACTION_GET_CONTENT
        intent.type = "image/*"
        val chooser = Intent.createChooser(intent,"Choose a picture")
        launcherIntentGallery.launch(chooser)
    }

    private fun uploadFile() {
        showLoading(true)
        val descriptionValue = binding.editDesc.text?.toString()?.trim()
        if (attachFile != null && descriptionValue!!.isNotEmpty()) {
            val file = attachFile as File
            val description = descriptionValue.toRequestBody("text/plain".toMediaType())
            val requestImageFile = file.asRequestBody("image/jpeg".toMediaTypeOrNull())
            val imageMultipart: MultipartBody.Part = MultipartBody.Part.createFormData(
                "photo",
                file.name,
                requestImageFile
            )
            val pref = AppDataStore.getInstance(dataStore)
            val authViewModel =
                ViewModelProvider(this, ViewModelFactory(pref))[AuthViewModel::class.java]
            authViewModel.loginToken().observe(this) { token: String? ->
                val bearerToken = HashMap<String, String>()
                bearerToken["Authorization"] = "Bearer $token"
                val client =
                    ApiConfig.getApiService().addNewStory(imageMultipart, description, bearerToken)
                client.enqueue(object : Callback<AddStoryResponse> {
                    override fun onResponse(
                        call: Call<AddStoryResponse>,
                        response: Response<AddStoryResponse>
                    ) {
                        showLoading(false)
                        if (response.isSuccessful) {
                            val responseBody = response.body()
                            Toast.makeText(
                                this@AddStoryActivity,
                                responseBody?.message,
                                Toast.LENGTH_SHORT
                            ).show()
                            val intent = Intent(this@AddStoryActivity, MainActivity::class.java)
                            startActivity(intent)
                            finish()
                        } else {
                            Toast.makeText(this@AddStoryActivity, "Gagal Upload", Toast.LENGTH_SHORT)
                                .show()
                        }
                    }

                    override fun onFailure(call: Call<AddStoryResponse>, t: Throwable) {
                        Toast.makeText(this@AddStoryActivity, "Gagal Upload $t", Toast.LENGTH_SHORT)
                            .show()
                    }
                })
            }
        } else {
            showLoading(false)
            Toast.makeText(
                this@AddStoryActivity,
                getString(R.string.story_empty),
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    private fun showLoading(isLoading: Boolean) {
        if (isLoading) {
            binding.progressBar.visibility = View.VISIBLE
        } else {
            binding.progressBar.visibility = View.GONE
        }
    }
}