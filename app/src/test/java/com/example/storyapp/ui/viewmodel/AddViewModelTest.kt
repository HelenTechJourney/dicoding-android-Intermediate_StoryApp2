package com.example.storyapp.ui.viewmodel

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.MutableLiveData
import com.example.storyapp.utils.getOrAwaitValue
import com.google.android.gms.maps.model.LatLng
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.Mockito.verify
import org.mockito.Mockito.`when`
import org.mockito.junit.MockitoJUnitRunner
import java.io.File

@RunWith(MockitoJUnitRunner::class)
class AddViewModelTest {
    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    @Mock
    private lateinit var storyViewModel: AddViewModel
    private var mockFile = File("fileName")

    @Before
    fun setUp() {
        storyViewModel = Mockito.mock(AddViewModel::class.java)
    }

    @Test
    fun `when message should return the right data and not null`() {
        val expectedMessage = MutableLiveData<String>()
        expectedMessage.value = "Success Uploaded"

        `when`(storyViewModel.message).thenReturn(expectedMessage)

        val actualMessage = storyViewModel.message.getOrAwaitValue()

        verify(storyViewModel).message
        assertNotNull(actualMessage)
        assertEquals(expectedMessage.value, actualMessage)
    }

    @Test
    fun `when loading state should return the right data and not null`() {
        val expectedLoadingData = MutableLiveData<Boolean>()
        expectedLoadingData.value = true

        `when`(storyViewModel.isLoading).thenReturn(expectedLoadingData)

        val actualLoading = storyViewModel.isLoading.getOrAwaitValue()

        verify(storyViewModel).isLoading
        assertNotNull(actualLoading)
        assertEquals(expectedLoadingData.value, actualLoading)
    }

    @Test
    fun `verify upload function is working`() {
        val expectedMessage = MutableLiveData<String>()
        expectedMessage.value = "Success Uploaded"

        val requestImageFile = mockFile.asRequestBody("image/jpeg".toMediaTypeOrNull())
        val imageMultipart: MultipartBody.Part = MultipartBody.Part.createFormData(
            "photo",
            "fileName",
            requestImageFile
        )
        val description: RequestBody = "ini description".toRequestBody("text/plain".toMediaType())
        val latlng = LatLng(1.1, 1.1)
        val token = "test token"

        storyViewModel.upload(imageMultipart, description, token, latlng.latitude, latlng.longitude )

        verify(storyViewModel).upload(
            imageMultipart,
            description,
            token,
            latlng.latitude,
            latlng.longitude
        )

        `when`(storyViewModel.message).thenReturn(expectedMessage)

        val actualStories = storyViewModel.message.getOrAwaitValue()

        verify(storyViewModel).message
        assertNotNull(actualStories)
        assertEquals(expectedMessage.value, actualStories)
    }
}