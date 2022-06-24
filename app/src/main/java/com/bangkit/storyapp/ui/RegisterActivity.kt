package com.bangkit.storyapp.ui

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.ContentValues.TAG
import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.WindowInsets
import android.view.WindowManager
import android.widget.Toast
import androidx.core.util.Pair
import androidx.core.app.ActivityOptionsCompat
import com.bangkit.storyapp.api.ApiConfig
import com.bangkit.storyapp.R
import com.bangkit.storyapp.response.RegisterResponse
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

    private fun setupAction() {
        binding.btnLogin.setOnClickListener {
            val moveIntent = Intent(this, LoginActivity::class.java)
            val optionCombat: ActivityOptionsCompat =
                ActivityOptionsCompat.makeSceneTransitionAnimation(
                    this@RegisterActivity,
                    Pair(binding.imageviewHeader, "logo")
                )

            startActivity(moveIntent, optionCombat.toBundle())
        }
        binding.signupButton.setOnClickListener {
            showLoading(true)
            if(registerValidation()) {
                val client = ApiConfig.getApiService().register(name, email, password)
                client.enqueue(object : Callback,
                    retrofit2.Callback<RegisterResponse> {
                    override fun onResponse(
                        call: Call<RegisterResponse>,
                        response: Response<RegisterResponse>
                    ) {
                        showLoading(false)
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

    private fun playAnimation() {
        val titleTextView = ObjectAnimator.ofFloat(binding.titleTextView, View.ALPHA, 1f).setDuration(500)
        val nameTextView = ObjectAnimator.ofFloat(binding.nameTextView, View.ALPHA, 1f).setDuration(500)
        val nameEditTextLayout = ObjectAnimator.ofFloat(binding.nameEditTextLayout, View.ALPHA, 1f).setDuration(500)
        val emailTextView = ObjectAnimator.ofFloat(binding.emailTextView, View.ALPHA, 1f).setDuration(500)
        val emailEditTextLayout = ObjectAnimator.ofFloat(binding.emailEditTextLayout, View.ALPHA, 1f).setDuration(500)
        val passwordTextView = ObjectAnimator.ofFloat(binding.passwordTextView, View.ALPHA, 1f).setDuration(500)
        val passwordEditTextLayout = ObjectAnimator.ofFloat(binding.passwordEditTextLayout, View.ALPHA, 1f).setDuration(500)
        val signupButton = ObjectAnimator.ofFloat(binding.signupButton, View.ALPHA, 1f).setDuration(500)
        val registerTextView = ObjectAnimator.ofFloat(binding.registerTextView, View.ALPHA, 1f).setDuration(500)
        val btnLogin = ObjectAnimator.ofFloat(binding.btnLogin, View.ALPHA, 1f).setDuration(500)

        val name = AnimatorSet().apply {
            playTogether(nameTextView, nameEditTextLayout)
        }

        val email = AnimatorSet().apply {
            playTogether(emailTextView, emailEditTextLayout)
        }

        val password = AnimatorSet().apply {
            playTogether(passwordTextView, passwordEditTextLayout)
        }

        val login = AnimatorSet().apply {
            playTogether(registerTextView, btnLogin)
        }

        AnimatorSet().apply {
            playSequentially(titleTextView, name, email, password, signupButton, login)
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