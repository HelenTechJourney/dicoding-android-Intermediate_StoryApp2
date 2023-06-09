package com.example.storyapp.ui.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.storyapp.di.Injection
import com.example.storyapp.remote.response.UserPreference

class ViewModelFactory(private val pref: UserPreference) :
    ViewModelProvider.NewInstanceFactory() {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(DataStoreViewModel::class.java)) {
            return DataStoreViewModel(pref) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class: " + modelClass.name)
    }
}
class RepoViewModelFactory (private val context: Context): ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        when {
            modelClass.isAssignableFrom(LoginViewModel::class.java) -> {
                @Suppress("UNCHECKED_CAST")
                return LoginViewModel(Injection.provideRepository(context)) as T
            }
            modelClass.isAssignableFrom(SignupViewModel::class.java) -> {
                @Suppress("UNCHECKED_CAST")
                return SignupViewModel(Injection.provideRepository(context)) as T
            }
            modelClass.isAssignableFrom(ListStoryViewModel::class.java) -> {
                @Suppress("UNCHECKED_CAST")
                return ListStoryViewModel(Injection.provideRepository(context)) as T
            }
            modelClass.isAssignableFrom(DetailViewModel::class.java) -> {
                @Suppress("UNCHECKED_CAST")
                return DetailViewModel(Injection.provideRepository(context)) as T
            }
            modelClass.isAssignableFrom(MapsViewModel::class.java) -> {
                @Suppress("UNCHECKED_CAST")
                return MapsViewModel(Injection.provideRepository(context)) as T
            }
            modelClass.isAssignableFrom(AddViewModel::class.java) -> {
                @Suppress("UNCHECKED_CAST")
                return AddViewModel(Injection.provideRepository(context)) as T
            }
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}