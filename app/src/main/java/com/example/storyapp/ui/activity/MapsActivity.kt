package com.example.storyapp.ui.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.storyapp.R
import com.example.storyapp.databinding.ActivityMapsBinding
import com.example.storyapp.di.LocationConverter
import com.example.storyapp.remote.response.ListStoryItem
import com.example.storyapp.remote.response.UserPreference
import com.example.storyapp.ui.adapter.MapsAdapter
import com.example.storyapp.ui.viewmodel.DataStoreViewModel
import com.example.storyapp.ui.viewmodel.MapsViewModel
import com.example.storyapp.ui.viewmodel.RepoViewModelFactory
import com.example.storyapp.ui.viewmodel.ViewModelFactory
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.MarkerOptions

class MapsActivity : AppCompatActivity(), OnMapReadyCallback {
    private val viewModel: MapsViewModel by viewModels {
        RepoViewModelFactory(this)
    }
    private lateinit var mMap: GoogleMap
    private lateinit var binding: ActivityMapsBinding
    private var storiesLocation = listOf<ListStoryItem>()

    companion object {
        const val DEFAULT_ZOOM = 15f
        const val INITIAL_ZOOM = 6f
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMapsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val pref = UserPreference.getInstance(dataStore)
        val dataStoreViewModel =
            ViewModelProvider(this, ViewModelFactory(pref))[DataStoreViewModel::class.java]

        val layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        binding.rvMaps.layoutManager = layoutManager
        val itemDecoration = DividerItemDecoration(this, layoutManager.orientation)
        binding.rvMaps.addItemDecoration(itemDecoration)

        supportActionBar?.title = "Google Maps"
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        dataStoreViewModel.getToken().observe(this) {
            viewModel.getMapsStories(it)
        }
        viewModel.listUser.observe(this) {
            setMapsStories(it)
        }
        viewModel.message.observe(this) {
            showToast(it)
        }
        viewModel.isLoading.observe(this) {
            showLoading(it)
        }
    }

    private fun setMapsStories(mapsStory: List<ListStoryItem>?) {
        val listUserAdapter = MapsAdapter(mapsStory!!)
        binding.rvMaps.adapter = listUserAdapter
        setMarker(mapsStory)

        listUserAdapter.setOnItemClickCallback(object : MapsAdapter.OnItemClickCallback {
            override fun onItemClicked(data: ListStoryItem) {
                val posisition = LocationConverter.toLatlng(data.lat, data.lon)
                if (posisition != null) {
                    mMap.animateCamera(
                        CameraUpdateFactory.newLatLngZoom(
                            posisition, DEFAULT_ZOOM
                        )
                    )
                } else {
                    Toast.makeText(
                        this@MapsActivity,
                        getString(R.string.no_location),
                        Toast.LENGTH_SHORT
                    ).show()
                }

            }
        })
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.map_options, menu)
        return true
    }
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                val intent = Intent(this@MapsActivity, ListStoryActivity::class.java)
                startActivity(intent)
                true
            }
            R.id.normal_type -> {
                mMap.mapType = GoogleMap.MAP_TYPE_NORMAL
                true
            }
            R.id.satellite_type -> {
                mMap.mapType = GoogleMap.MAP_TYPE_SATELLITE
                true
            }
            R.id.terrain_type -> {
                mMap.mapType = GoogleMap.MAP_TYPE_TERRAIN
                true
            }
            R.id.hybrid_type -> {
                mMap.mapType = GoogleMap.MAP_TYPE_HYBRID
                true
            }
            else -> {
                super.onOptionsItemSelected(item)
            }
        }
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        mMap.uiSettings.isZoomControlsEnabled = true
        mMap.uiSettings.isIndoorLevelPickerEnabled = true
        mMap.uiSettings.isCompassEnabled = true
        mMap.uiSettings.isMapToolbarEnabled = true
    }

    private fun showLoading(isLoading: Boolean) {
        if (isLoading) {
            binding.progressBar.visibility = View.VISIBLE
        } else {
            binding.progressBar.visibility = View.GONE
        }
    }
    private fun showToast(message: String) {
        if (message != "Stories fetched successfully") {
            binding.tvError.visibility = View.VISIBLE
            Toast.makeText(
                this,
                "${getString(R.string.error_load)} $message",
                Toast.LENGTH_LONG
            ).show()
        } else {
            Log.d("TOKEN",message)
            binding.tvError.visibility = View.GONE
        }
    }

    private fun setMarker(stories: List<ListStoryItem>) {
        if (stories.isNotEmpty()) {
            for (story in stories) {
                val position = LocationConverter.toLatlng(story.lat, story.lon)
                val address = LocationConverter.getStringAddress(position, this)
                if (position != null) {//pengecekan data lokasi
                    storiesLocation = storiesLocation + story
                    mMap.addMarker(
                        MarkerOptions().position(position).title(story.name).snippet(address)
                    )

                }
            }
        }
        if (storiesLocation.isNotEmpty()) {
            val position =
                LocationConverter.toLatlng(storiesLocation[0].lat, storiesLocation[0].lon)!!
            mMap.animateCamera(
                CameraUpdateFactory.newLatLngZoom(
                    position, INITIAL_ZOOM
                )
            )
        }
    }
}
