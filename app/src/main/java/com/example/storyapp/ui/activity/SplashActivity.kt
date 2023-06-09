package com.example.storyapp.ui.activity

import android.animation.ObjectAnimator
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.ViewPropertyAnimator
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.example.storyapp.databinding.ActivitySplashBinding
import com.example.storyapp.remote.response.UserPreference
import com.example.storyapp.ui.viewmodel.DataStoreViewModel
import com.example.storyapp.ui.viewmodel.ViewModelFactory

class SplashActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySplashBinding
    private var startSplash: ViewPropertyAnimator? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySplashBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.hide()

        val img = binding.splashBlob
        val pref = UserPreference.getInstance(dataStore)
        val loginViewModel =
            ViewModelProvider(this, ViewModelFactory(pref))[DataStoreViewModel::class.java]

        loginViewModel.getLoginState().observe(this) {
            ObjectAnimator.ofFloat(binding.splashImage, View.ALPHA, 1f).apply {
                start()
            }

            startSplash = img.animate().setDuration(3000L).alpha(1f).withEndAction {
                if (it) {
                    startActivity(Intent(this, ListStoryActivity::class.java))
                    finish()
                } else {
                    startActivity(Intent(this, WelcomeActivity::class.java))
                    finish()
                }
            }
        }
    }

    override fun onDestroy() {
        startSplash?.cancel()
        super.onDestroy()
    }
}