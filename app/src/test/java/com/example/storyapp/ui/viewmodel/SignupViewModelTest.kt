package com.example.storyapp.ui.viewmodel

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.MutableLiveData
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
class SignupViewModelTest{
    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    @Mock
    private lateinit var storyViewModel: SignupViewModel

    @Before
    fun setUp() {
        storyViewModel = mock(SignupViewModel::class.java)
    }

    @Test
    fun `when signup message should return right data and not null`() {
        val expectedSignup = MutableLiveData<String>()
        expectedSignup.value = "User Created"

        `when`(storyViewModel.message).thenReturn(expectedSignup)
        val actualMessage = storyViewModel.message.getOrAwaitValue()
        verify(storyViewModel).message

        assertNotNull(actualMessage)
        assertEquals(expectedSignup.value, actualMessage)
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
    fun `verify getSignupResponse function is working`() {
        val dummyRequestSignup = DataDummy.generateDummyRequestSignUp()
        val expectedRegisterMessage = MutableLiveData<String>()
        expectedRegisterMessage.value = "User Created"

        storyViewModel.getSignupResponse(dummyRequestSignup)

        verify(storyViewModel).getSignupResponse(dummyRequestSignup)

        `when`(storyViewModel.message).thenReturn(expectedRegisterMessage)

        val actualData = storyViewModel.message.getOrAwaitValue {  }

        verify(storyViewModel).message
        assertNotNull(actualData)
        assertEquals(expectedRegisterMessage.value, actualData)
    }
}