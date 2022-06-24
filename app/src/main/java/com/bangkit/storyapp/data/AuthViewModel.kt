package com.bangkit.storyapp.data

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

class AuthViewModel(private val pref: AppDataStore) : ViewModel() {

    fun loginToken(): LiveData<String?> {
        return pref.getToken().asLiveData()
    }

    fun saveToken(token: String) {
        viewModelScope.launch {
            pref.saveToken(token)
        }
    }

    fun clearToken() {
        viewModelScope.launch {
            pref.clearToken()
        }
    }
}