package com.example.storyapp.ui.activity

import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.example.storyapp.databinding.ActivityDetailStoryBinding
import com.example.storyapp.di.LocationConverter
import com.example.storyapp.remote.response.ListStoryItem
import com.example.storyapp.remote.response.Story
import com.example.storyapp.remote.response.UserPreference
import com.example.storyapp.ui.viewmodel.*

@Suppress("DEPRECATION", "NAME_SHADOWING")
class DetailStoryActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDetailStoryBinding
    private val viewModel: DetailViewModel by viewModels{
        RepoViewModelFactory(this)
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailStoryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.title = "Detail Story"
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        val story = if (Build.VERSION.SDK_INT >= 33) {
            intent.getParcelableExtra(EXTRA_STORY, ListStoryItem::class.java)
        } else {
            intent.getParcelableExtra(EXTRA_STORY)
        }
//        story?.let { setDetailStory(it) }
        story?.let{story->
            val pref = UserPreference.getInstance(dataStore)
            val dataStoreViewModel =
                ViewModelProvider(this, ViewModelFactory(pref))[DataStoreViewModel::class.java]
            dataStoreViewModel.getToken().observe(this) {token->
                viewModel.getDetailStories(token, story.id)
            }
        }

        viewModel.isLoading.observe(this) {
            showLoading(it)
        }
        viewModel.detailUser.observe(this) { detailUser ->
            detailUser?.let{setDetailStory(it)}
        }
    }

    private fun showLoading(isLoading: Boolean) {
        if (isLoading) {
            binding.progressBar.visibility = View.VISIBLE
        } else {
            binding.progressBar.visibility = View.GONE
        }
    }

    private fun setDetailStory(detailStory: Story) {
        binding.apply {
            tvDetailName.text = detailStory.name
            tvDetailDescription.text = detailStory.description
        }
        binding.tvDetailLocation.text = LocationConverter.getStringAddress(LocationConverter.toLatlng(detailStory.lat, detailStory.lon),
            this)
        Glide.with(this)
            .load(detailStory.photoUrl)
            .into(binding.ivDetailPhoto)
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    companion object {
        const val EXTRA_STORY = "extra_story"
    }
}