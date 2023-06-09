package com.example.storyapp.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.example.storyapp.data.StoryRepository
import okhttp3.MultipartBody
import okhttp3.RequestBody

class AddViewModel(private val provideRepository: StoryRepository) : ViewModel() {
    val message: LiveData<String> = provideRepository.message

    val isLoading: LiveData<Boolean> = provideRepository.isLoading

    fun upload(
        photo: MultipartBody.Part,
        desc: RequestBody,
        token: String,
        lat: Double?,
        lng: Double?
    ){
        provideRepository.upload(photo,desc,token,lat,lng)
    }
}