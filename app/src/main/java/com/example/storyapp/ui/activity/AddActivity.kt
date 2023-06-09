package com.example.storyapp.ui.activity

import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.lifecycle.ViewModelProvider
import com.example.storyapp.R
import com.example.storyapp.databinding.ActivityAddBinding
import com.example.storyapp.di.LocationConverter
import com.example.storyapp.di.createCustomTempFile
import com.example.storyapp.di.uriToFile
import com.example.storyapp.remote.response.UserPreference
import com.example.storyapp.ui.viewmodel.*
import com.google.android.gms.maps.model.LatLng
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream

@Suppress("DEPRECATION")
class AddActivity : AppCompatActivity() {
    private lateinit var currentPhotoPath: String
    private lateinit var binding: ActivityAddBinding
    private lateinit var token: String
    private val MAPS_LOCATION_REQUEST_CODE = 1
    private var latlng: LatLng? = null
    private var getFile: File? = null
    private val viewModel: AddViewModel by viewModels {
        RepoViewModelFactory(this)
    }
    private val resultContract =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult? ->
            if (result?.resultCode == Activity.RESULT_OK) {

                val lat = result.data?.getDoubleExtra(MapsLocationActivity.EXTRA_LAT, 0.0)
                val lng = result.data?.getDoubleExtra(MapsLocationActivity.EXTRA_LNG, 0.0)
                if (lat != null && lng != null) {
                    latlng = LatLng(lat, lng)
                    binding.tvLocation.text = LocationConverter.getStringAddress(latlng, this)
                }
            }
        }

    companion object {
        private val REQUIRED_PERMISSIONS = arrayOf(android.Manifest.permission.CAMERA)
        private const val REQUEST_CODE_PERMISSIONS = 10
        private const val MAXIMAL_SIZE = 1000000
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.title = "Add Story"
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        val pref = UserPreference.getInstance(dataStore)
        val dataStoreViewModel =
            ViewModelProvider(this, ViewModelFactory(pref))[DataStoreViewModel::class.java]

        dataStoreViewModel.getToken().observe(this) {
            token = it
        }

        viewModel.message.observe(this) {
            showToast(it)
        }

        viewModel.isLoading.observe(this) {
            showLoading(it)
        }

        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult? ->
            if (result?.resultCode == Activity.RESULT_OK) {

                val lat = result.data?.getDoubleExtra(MapsLocationActivity.EXTRA_LAT, 0.0)
                val lng = result.data?.getDoubleExtra(MapsLocationActivity.EXTRA_LNG, 0.0)
                if (lat != null && lng != null) {
                    latlng = LatLng(lat, lng)
                    binding.tvLocation.text = LocationConverter.getStringAddress(latlng, this)
                }
            }
        }

        if (!allPermissionsGranted()) {
            ActivityCompat.requestPermissions(
                this,
                REQUIRED_PERMISSIONS,
                REQUEST_CODE_PERMISSIONS
            )
        }

        binding.checkLocation.setOnClickListener{
            val intent = Intent(this, MapsLocationActivity::class.java)
            resultContract.launch(intent)
        }
        binding.cameraButton.setOnClickListener { startTakePhoto() }
        binding.galleryButton.setOnClickListener { startGallery() }
        binding.uploadButton.setOnClickListener { uploadStory() }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_CODE_PERMISSIONS) {
            if (!allPermissionsGranted()) {
                Toast.makeText(
                    this,
                    "Tidak mendapatkan permission.",
                    Toast.LENGTH_SHORT
                ).show()
                finish()
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == MAPS_LOCATION_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            val address = data?.getStringExtra("address")

            binding.tvLocation.text = address

            val isChecked = !address.isNullOrEmpty()
            binding.checkLocation.isChecked = isChecked
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> showAlertDialog()
        }
        return super.onOptionsItemSelected(item)
    }

    private fun showAlertDialog() {
        val dialogBuilder = AlertDialog.Builder(this)
        val alert = dialogBuilder.create()
        dialogBuilder
            .setTitle(getString(R.string.cancel))
            .setMessage(getString(R.string.message_cancel))
            .setPositiveButton(getString(R.string.yes)) { _, _ ->
                startActivity(Intent(this, ListStoryActivity::class.java))
                finish()
            }
            .setNegativeButton(getString(R.string.no)) { _, _ ->
                alert.cancel()
            }
            .show()
    }

    private fun allPermissionsGranted() = REQUIRED_PERMISSIONS.all {
        ContextCompat.checkSelfPermission(baseContext, it) == PackageManager.PERMISSION_GRANTED
    }

    private fun reduceFileImage(file: File): File {
        val bitmap = BitmapFactory.decodeFile(file.path)
        var compressQuality = 100
        var streamLength: Int
        do {
            val bmpStream = ByteArrayOutputStream()
            bitmap.compress(Bitmap.CompressFormat.JPEG, compressQuality, bmpStream)
            val bmpPicByteArray = bmpStream.toByteArray()
            streamLength = bmpPicByteArray.size
            compressQuality -= 5
        } while (streamLength > MAXIMAL_SIZE)
        bitmap.compress(Bitmap.CompressFormat.JPEG, compressQuality, FileOutputStream(file))
        return file
    }

    private fun startGallery() {
        val intent = Intent()
        intent.action = Intent.ACTION_GET_CONTENT
        intent.type = "image/*"
        val chooser = Intent.createChooser(intent, "Choose a Picture")
        launcherIntentGallery.launch(chooser)
    }

    private fun startTakePhoto() {
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        intent.resolveActivity(packageManager)

        createCustomTempFile(application).also {
            val photoURI: Uri = FileProvider.getUriForFile(
                this@AddActivity,
                "com.example.storyapp",
                it
            )
            currentPhotoPath = it.absolutePath
            intent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
            launcherIntentCamera.launch(intent)
        }
    }
    private val launcherIntentCamera = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) {
        if (it.resultCode == RESULT_OK) {
            val myFile = File(currentPhotoPath)

            myFile.let { file ->
                getFile = file
                binding.previewImageView.setImageBitmap(BitmapFactory.decodeFile(file.path))
            }
        }
    }

    private val launcherIntentGallery = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == RESULT_OK) {
            val selectedImg = result.data?.data as Uri
            selectedImg.let { uri ->
                val myFile = uriToFile(uri, this)
                getFile = myFile
                binding.previewImageView.setImageURI(uri)
            }
        }
    }

    private fun uploadStory() {
            if (getFile != null) {
                val file = reduceFileImage(getFile as File)
                val description = binding.etDesc.text.toString().toRequestBody("text/plain".toMediaType())
                val requestImageFile = file.asRequestBody("image/jpeg".toMediaTypeOrNull())
                val imageMultipart: MultipartBody.Part = MultipartBody.Part.createFormData(
                    "photo",
                    file.name,
                    requestImageFile
                )
                viewModel.upload(
                    imageMultipart,
                    description,
                    token,
                    latlng?.latitude,
                    latlng?.longitude)
            }else if(binding.etDesc.text.toString().trim().isEmpty()) {
                    Toast.makeText(
                        this,
                        getString(R.string.input_desc),
                        Toast.LENGTH_SHORT
                    ).show()
            } else {
                Toast.makeText(
                    this,
                    getString(R.string.input_photo),
                    Toast.LENGTH_SHORT
                ).show()
            }

    }

    private fun showLoading(isLoading: Boolean) {
        if (isLoading) {
            binding.progressBar.visibility = View.VISIBLE
        } else {
            binding.progressBar.visibility = View.GONE
        }
    }

    private fun showToast(message: String) {
        if (message.contains("Story created successfully")) {
            Toast.makeText(
                this,
                "$message",
                Toast.LENGTH_LONG
            ).show()
            startActivity(Intent(this@AddActivity, ListStoryActivity::class.java))
            finish()
        }
    }
}