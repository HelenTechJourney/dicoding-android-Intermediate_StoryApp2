package com.example.storyapp.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.example.storyapp.data.StoryRepository
import com.example.storyapp.remote.response.Story

class DetailViewModel(private val provideRepository: StoryRepository): ViewModel() {

    val message: LiveData<String> = provideRepository.message

    val isLoading: LiveData<Boolean> = provideRepository.isLoading

    val detailUser: LiveData<Story> = provideRepository.detailUser

    fun getDetailStories(token:String, id:String){
        provideRepository.getDetail(token, id)
    }
}