package com.example.storyapp.ui.viewmodel

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.MutableLiveData
import com.example.storyapp.remote.response.ListStoryItem
import com.example.storyapp.utils.DataDummy
import com.example.storyapp.utils.getOrAwaitValue
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito.*
import org.mockito.junit.MockitoJUnitRunner

@RunWith(MockitoJUnitRunner::class)
class MapsViewModelTest {
    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    @Mock
    private lateinit var storyViewModel: MapsViewModel
    private val dummyStories = DataDummy.generateDummyStoryEntity()

    @Before
    fun setUp() {
        storyViewModel = mock(MapsViewModel::class.java)
    }


    @Test
    fun `when listUser should return the right data and not null`() {
        val expectedStories = MutableLiveData<List<ListStoryItem>>()
        expectedStories.value = dummyStories

        `when`(storyViewModel.listUser).thenReturn(expectedStories)

        val actualStories = storyViewModel.listUser.getOrAwaitValue()

        verify(storyViewModel).listUser

        assertNotNull(actualStories)
        assertEquals(expectedStories.value, actualStories)
        assertEquals(dummyStories.size, actualStories!!.size)
    }


    @Test
    fun `when message should return the right data and not null`() {
        val expectedMessage = MutableLiveData<String>()
        expectedMessage.value = "Stories fetched Successfully"

        `when`(storyViewModel.message).thenReturn(expectedMessage)

        val actualRegisterMessage = storyViewModel.message.getOrAwaitValue()

        verify(storyViewModel).message
        assertNotNull(actualRegisterMessage)
        assertEquals(expectedMessage.value, actualRegisterMessage)
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
    fun `verify getMapsStories function is working`() {
        val expectedStories = MutableLiveData<List<ListStoryItem>>()
        expectedStories.value = dummyStories

        val token = "test token"
        storyViewModel.getMapsStories(token)
        verify(storyViewModel).getMapsStories(token)

        `when`(storyViewModel.listUser).thenReturn(expectedStories)

        val actualStories = storyViewModel.listUser.getOrAwaitValue()

        verify(storyViewModel).listUser

        assertNotNull(actualStories)
        assertEquals(expectedStories.value, actualStories)
        assertEquals(dummyStories.size, actualStories!!.size)
    }

    //data empty

    @Test
    fun `verify getMapsStories empty should return empty and not null`() {
        val expectedStories = MutableLiveData<List<ListStoryItem>>()
        expectedStories.value = listOf()

        val token = "test token"
        storyViewModel.getMapsStories(token)
        verify(storyViewModel).getMapsStories(token)

        `when`(storyViewModel.listUser).thenReturn(expectedStories)

        val actualStories = storyViewModel.listUser.getOrAwaitValue()

        verify(storyViewModel).listUser

        assertNotNull(actualStories)
        assertTrue(actualStories!!.isEmpty())
    }
}