package com.example.storyapp.ui.activity

import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.Resources
import android.location.Location
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import com.example.storyapp.R
import com.example.storyapp.databinding.ActivityMapsLocationBinding
import com.example.storyapp.di.LocationConverter
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import com.google.android.gms.tasks.Task

class MapsLocationActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private lateinit var binding: ActivityMapsLocationBinding

    companion object {
        private const val TAG = "MapsLocationActivity"
        var currentLagLng: LatLng? = null
        var selectedLocation: LatLng? = null
        const val DEFAULT_ZOOM = 15.0f
        const val EXTRA_LAT = "LAT"
        const val EXTRA_LNG = "LNG"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMapsLocationBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setButtonCurrentLocationEnable(false)
        setButtonPickPlaceEnable(false)

        binding.btnCurrentLocation.setOnClickListener {
            showAlertDialog(currentLagLng)
        }
        binding.btnPlacePicker.setOnClickListener {
            showAlertDialog(selectedLocation)
        }

        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        getMyLocation()
        getDeviceLocation()
        setMapStyle()
        mMap.setOnMapClickListener {
            selectedLocation = it
            val markerOptions = MarkerOptions()
            markerOptions.position(it)

            markerOptions.title(LocationConverter.getStringAddress(it, this))
            mMap.clear()
            val location = CameraUpdateFactory.newLatLngZoom(
                it, 15f
            )
            mMap.animateCamera(location)
            mMap.addMarker(markerOptions)
            setButtonPickPlaceEnable(true)
        }
    }

    private fun setMapStyle() {
        try {
            val success =
                mMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(this, R.raw.map_style))
            if (!success) {
                Log.e(TAG, "Style parsing failed.")
            }
        } catch (exception: Resources.NotFoundException) {
            Log.e(TAG, "Can't find style. Error: ", exception)
        }
    }

    private fun showAlertDialog(latlng: LatLng?) {
        val address = LocationConverter.getStringAddress(latlng, this)
        val builder = AlertDialog.Builder(this)
        val alert = builder.create()
        builder
            .setTitle(getString(R.string.use_this_location))
            .setMessage(address)
            .setPositiveButton(getString(R.string.yes)) { _, _ ->
                sendResultLocation(latlng)
            }
            .setNegativeButton(getString(R.string.no)) { _, _ ->
                alert.cancel()
            }
            .show()
    }

    private fun sendResultLocation(latlng: LatLng?) {
        val intent = Intent()
        if (latlng != null) {
            intent.putExtra(EXTRA_LAT, latlng.latitude)
            intent.putExtra(EXTRA_LNG, latlng.longitude)
        }
        setResult(Activity.RESULT_OK, intent)
        finish()
    }

    private fun defaultLocation() = LatLng(-34.0, 151.0)

    private fun isPremissionGranted(): Boolean {
        return ContextCompat.checkSelfPermission(
            this.applicationContext,
            android.Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
    }

    private val requestPermissionLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { isGranted: Boolean ->
            if (isGranted) {
                getDeviceLocation()
            }
        }

    private fun getMyLocation() {
        if (ContextCompat.checkSelfPermission(
                this.applicationContext,
                android.Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            mMap.isMyLocationEnabled = true
        } else {
            requestPermissionLauncher.launch(android.Manifest.permission.ACCESS_FINE_LOCATION)
        }
    }

    private fun setButtonCurrentLocationEnable(isEnable: Boolean) {
        binding.btnCurrentLocation.isEnabled = isEnable
    }

    private fun setButtonPickPlaceEnable(isEnable: Boolean) {
        binding.btnPlacePicker.isEnabled = isEnable
    }

    private fun getDeviceLocation() {
        try {
            if (isPremissionGranted()) {
                val locationResult: Task<Location> =
                    LocationServices.getFusedLocationProviderClient(this).lastLocation
                locationResult.addOnSuccessListener {
                    if (it != null) {
                        currentLagLng = LatLng(
                            it.latitude,
                            it.longitude
                        )
                        setButtonCurrentLocationEnable(true)
                        mMap.addMarker(
                            MarkerOptions().position(
                                LatLng(
                                    it.latitude,
                                    it.longitude
                                )
                            ).title(getString(R.string.my_location))
                        )
                        mMap.moveCamera(
                            CameraUpdateFactory.newLatLngZoom(
                                LatLng(
                                    it.latitude,
                                    it.longitude
                                ), DEFAULT_ZOOM
                            )
                        )
                    } else {
                        setButtonCurrentLocationEnable(false)
                        Toast.makeText(
                            this,
                            getString(R.string.no_current_location),
                            Toast.LENGTH_SHORT
                        ).show()
                        mMap.moveCamera(
                            CameraUpdateFactory
                                .newLatLngZoom(defaultLocation(), DEFAULT_ZOOM)
                        )
                        mMap.isMyLocationEnabled = false
                    }
                }
            } else {
                requestPermissionLauncher.launch(android.Manifest.permission.ACCESS_FINE_LOCATION)
            }
        } catch (e: SecurityException) {
            Log.e(getString(R.string.message_error), e.message, e)
        }
    }
}