package com.example.storyapp.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.example.storyapp.data.StoryRepository
import com.example.storyapp.remote.response.LoginResponse
import com.example.storyapp.remote.response.RequestLogin

class LoginViewModel(private val provideRepository: StoryRepository) : ViewModel() {

    val message: LiveData<String> = provideRepository.message

    val isLoading: LiveData<Boolean> = provideRepository.isLoading

    var userLogin: LiveData<LoginResponse> = provideRepository.userLogin

    fun getLoginResponse(requestLogin: RequestLogin) {
        provideRepository.getLoginResponse(requestLogin)
    }
}