package com.bangkit.storyapp.ui

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.annotation.SuppressLint
import android.content.ContentValues.TAG
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.core.util.Pair
import android.util.Log
import android.view.View
import android.view.WindowInsets
import android.view.WindowManager
import android.widget.Toast
import androidx.core.app.ActivityOptionsCompat
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.ViewModelProvider
import com.bangkit.storyapp.LoginResponse
import com.bangkit.storyapp.R
import com.bangkit.storyapp.api.ApiConfig
import com.bangkit.storyapp.databinding.ActivityLoginBinding
import com.bangkit.storyapp.datastore.AppDataStore
import com.bangkit.storyapp.datastore.AuthViewModel
import com.bangkit.storyapp.datastore.ViewModelFactory
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
        playAnimation()
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
            val moveIntent = Intent(this, RegisterActivity::class.java)
            val optionCombat: ActivityOptionsCompat =
                ActivityOptionsCompat.makeSceneTransitionAnimation(
                    this@LoginActivity,
                    Pair(binding.imageviewHeader, "logo")
                )

            startActivity(moveIntent, optionCombat.toBundle())
        }
        binding.loginButton.setOnClickListener {
            showLoading(true)
            if(loginValidation()) {
                val client = ApiConfig.getApiService().login(email, password)
                client.enqueue(object : Callback,
                    retrofit2.Callback<LoginResponse> {
                    override fun onResponse(
                        call: Call<LoginResponse>,
                        response: Response<LoginResponse>
                    ) {
                        showLoading(false)
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

    @SuppressLint("Recycle")
    private fun playAnimation() {
        val titleTextView = ObjectAnimator.ofFloat(binding.titleTextView, View.ALPHA, 1f).setDuration(500)
        val emailTextView = ObjectAnimator.ofFloat(binding.emailTextView, View.ALPHA, 1f).setDuration(500)
        val emailEditTextLayout = ObjectAnimator.ofFloat(binding.emailEditTextLayout, View.ALPHA, 1f).setDuration(500)
        val passwordTextView = ObjectAnimator.ofFloat(binding.passwordTextView, View.ALPHA, 1f).setDuration(500)
        val passwordEditTextLayout = ObjectAnimator.ofFloat(binding.passwordEditTextLayout, View.ALPHA, 1f).setDuration(500)
        val loginButton = ObjectAnimator.ofFloat(binding.loginButton, View.ALPHA, 1f).setDuration(500)
        val registerTextView = ObjectAnimator.ofFloat(binding.registerTextView, View.ALPHA, 1f).setDuration(500)
        val btnRegister = ObjectAnimator.ofFloat(binding.btnRegister, View.ALPHA, 1f).setDuration(500)

        val email = AnimatorSet().apply {
            playTogether(emailTextView, emailEditTextLayout)
        }

        val password = AnimatorSet().apply {
            playTogether(passwordTextView, passwordEditTextLayout)
        }

        val register = AnimatorSet().apply {
            playTogether(registerTextView, btnRegister)
        }

        AnimatorSet().apply {
            playSequentially(titleTextView, email, password, loginButton, register)
            start()
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