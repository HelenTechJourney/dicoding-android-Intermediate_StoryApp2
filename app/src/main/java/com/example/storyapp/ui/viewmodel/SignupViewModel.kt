package com.example.storyapp.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.example.storyapp.data.StoryRepository
import com.example.storyapp.remote.response.RequestSignup

class SignupViewModel(private val provideRepository: StoryRepository) : ViewModel() {

    val message: LiveData<String> = provideRepository.message

    val isLoading: LiveData<Boolean> = provideRepository.isLoading

    fun getSignupResponse(requestSignUp: RequestSignup) {
        provideRepository.getSignupResponse(requestSignUp)
    }
}