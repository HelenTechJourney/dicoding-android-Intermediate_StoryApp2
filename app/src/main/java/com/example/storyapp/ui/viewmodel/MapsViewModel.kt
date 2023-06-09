package com.example.storyapp.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.example.storyapp.data.StoryRepository
import com.example.storyapp.remote.response.ListStoryItem

class MapsViewModel(private val provideRepository: StoryRepository) : ViewModel() {

    val listUser: LiveData<List<ListStoryItem>?> = provideRepository.listUser

    val message: LiveData<String> = provideRepository.message

    val isLoading: LiveData<Boolean> = provideRepository.isLoading

    fun getMapsStories(token: String) {
        provideRepository.getAllMaps(token)
    }

}