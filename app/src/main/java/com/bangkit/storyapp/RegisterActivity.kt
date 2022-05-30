package com.bangkit.storyapp

import android.content.ContentValues.TAG
import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.WindowInsets
import android.view.WindowManager
import android.widget.Toast
import com.bangkit.storyapp.databinding.ActivityRegisterBinding
import retrofit2.Call
import javax.security.auth.callback.Callback
import retrofit2.Response

class RegisterActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRegisterBinding

    private lateinit var name: String
    private lateinit var email: String
    private lateinit var password: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
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
        binding.btnLogin.setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
        }
        binding.signupButton.setOnClickListener {
            // add ProgressBar
            if(registerValidation()) {
                val client = ApiConfig.getApiService().register(name, email, password)
                client.enqueue(object : Callback,
                    retrofit2.Callback<RegisterResponse> {
                    override fun onResponse(
                        call: Call<RegisterResponse>,
                        response: Response<RegisterResponse>
                    ) {
                        if (response.isSuccessful) {
                            Toast.makeText(
                                this@RegisterActivity,
                                response.body()?.message ?: "Account Created",
                                Toast.LENGTH_SHORT
                            ).show()

                            val moveIntent = Intent(this@RegisterActivity, LoginActivity::class.java)
                            startActivity(moveIntent)
                            finish()

                        } else {
                            Toast.makeText(
                                this@RegisterActivity,
                                response.body()?.message ?: "Failed Creating Account",
                                Toast.LENGTH_SHORT
                            ).show()
                            Log.e(TAG, "onFailure: ${response.message()}")
                        }
                    }

                    override fun onFailure(call: Call<RegisterResponse>, t: Throwable) {
                        Toast.makeText(
                            this@RegisterActivity,
                            "Failed Creating Account",
                            Toast.LENGTH_SHORT
                        ).show()
                        Log.e(TAG, "onFailure: ${t.message}")
                    }
                })
            }
        }
    }
    private fun registerValidation(): Boolean {
        with(binding) {
            name = nameEditText.text?.toString()?.trim()!!
            email = emailEditText.text?.toString()?.trim()!!
            password = passwordEditText.text?.toString()?.trim()!!
            val isValid: Boolean

            when {
                name.isEmpty() -> {
                    isValid = false
                    nameEditText.error = resources.getString(R.string.name_empty)
                }
                email.isEmpty() -> {
                    isValid = false
                    emailEditText.error = resources.getString(R.string.email_empty)
                }
                password.isEmpty() -> {
                    isValid = false
                    passwordEditText.error = resources.getString(R.string.password_empty)
                }
                else -> {
                    isValid = true
                }
            }
            return isValid
        }
    }
}