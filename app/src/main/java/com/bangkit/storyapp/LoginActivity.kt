package com.bangkit.storyapp

import android.content.ContentValues
import android.content.ContentValues.TAG
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.WindowInsets
import android.view.WindowManager
import android.widget.Toast
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.ViewModelProvider
import com.bangkit.storyapp.databinding.ActivityLoginBinding
import retrofit2.Call
import retrofit2.Response
import javax.security.auth.callback.Callback

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "login")

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding

    private lateinit var email: String
    private lateinit var password: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val pref = AppDataStore.getInstance(dataStore)
        val authViewModel = ViewModelProvider(this, ViewModelFactory(pref))[AuthViewModel::class.java]

        setupView()
        setupAction(authViewModel)
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

    private fun setupAction(authViewModel: AuthViewModel) {
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
                            val tokenLogin = response.body()?.loginResult?.token
                            tokenLogin?.let { token ->
                                authViewModel.saveToken(token)
                            }
                            Toast.makeText(
                                this@LoginActivity,
                                "Selamat datang, ${response.body()?.loginResult?.name
                                }",
                                Toast.LENGTH_SHORT
                            ).show()

                            val moveIntent = Intent(this@LoginActivity, MainActivity::class.java)
                            moveIntent.putExtra(MainActivity.TOKEN, tokenLogin)
                            startActivity(moveIntent)

                        } else {
                            Toast.makeText(
                                this@LoginActivity,
                                response.body()?.message ?: "Login Failed",
                                Toast.LENGTH_SHORT
                            ).show()
                            Log.e(TAG, "onFailure: ${response.message()}")
                        }
                    }

                    override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                        Toast.makeText(
                            this@LoginActivity,
                            "Login Failed",
                            Toast.LENGTH_SHORT
                        ).show()
                        Log.e(TAG, "onFailure: ${t.message}")
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