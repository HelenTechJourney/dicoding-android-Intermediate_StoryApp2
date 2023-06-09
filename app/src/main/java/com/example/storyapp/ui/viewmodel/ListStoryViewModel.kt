package com.example.storyapp.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.example.storyapp.data.StoryRepository
import com.example.storyapp.remote.response.ListStoryItem

class ListStoryViewModel(private val provideRepository: StoryRepository): ViewModel() {

//    val message: LiveData<String> = provideRepository.message

//    val isLoading: LiveData<Boolean> = provideRepository.isLoading

//    val listUser: LiveData<List<ListStoryItem>?> = provideRepository.listUser

    fun getPaging(token:String): LiveData<PagingData<ListStoryItem>> {
        return provideRepository.getPagingStories(token).cachedIn(viewModelScope)}

//    fun getAllStories(token:String){
//        provideRepository.getAllStories(token)
//    }
}