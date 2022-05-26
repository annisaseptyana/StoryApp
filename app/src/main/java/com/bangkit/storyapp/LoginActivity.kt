package com.bangkit.storyapp

import android.content.ContentValues
import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.WindowInsets
import android.view.WindowManager
import android.widget.Toast
import com.bangkit.storyapp.databinding.ActivityLoginBinding
import retrofit2.Call
import retrofit2.Response
import javax.security.auth.callback.Callback

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding

    private lateinit var email: String
    private lateinit var password: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupView()
        setupAction()
    }

    private fun setupView() {
        @Suppress("DEPRECATION")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.insetsController?.hide(WindowInsets.Type.statusBars())
        } else {
            window.setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN
            )
        }
        supportActionBar?.hide()
    }

    private fun setupAction() {
        binding.btnRegister.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
        }
        binding.loginButton.setOnClickListener {
            if(loginValidation()) {
                val client = ApiConfig.getApiService().login(email, password)
                client.enqueue(object : Callback,
                    retrofit2.Callback<LoginResponse> {
                    override fun onResponse(
                        call: Call<LoginResponse>,
                        response: Response<LoginResponse>
                    ) {
                        if (response.isSuccessful) {
                            Toast.makeText(this@LoginActivity, "Login Berhasil", Toast.LENGTH_SHORT).show()
                        } else {
                            Toast.makeText(this@LoginActivity, "Login tidak berhasil", Toast.LENGTH_SHORT).show()
                            Log.e(ContentValues.TAG, "onFailure: ${response.message()}")
                        }
                    }

                    override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                        Toast.makeText(this@LoginActivity, "Login Gagal", Toast.LENGTH_SHORT).show()
                        Log.e(ContentValues.TAG, "onFailure: ${t.message}")
                    }

                })
            }
        }
    }

    private fun loginValidation(): Boolean {
        with(binding) {
            email = emailEditText.text?.toString()?.trim()!!
            password = passwordEditText.text?.toString()?.trim()!!
            val isValid: Boolean

            when {
                email.isEmpty() -> {
                    //emailEditText.error = resources.getString(R.string.title_register_activity_layout_2)
                    isValid = false
                }
                password.isEmpty() -> {
                    //passwordEditText.error = resources.getString(R.string.title_register_activity_layout_3)
                    isValid = false
                }
                else -> {
                    isValid = true
                }
            }
            return isValid
        }
    }
}