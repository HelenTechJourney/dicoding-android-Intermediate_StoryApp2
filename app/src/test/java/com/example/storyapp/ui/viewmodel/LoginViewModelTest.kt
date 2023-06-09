package com.example.storyapp.ui.viewmodel

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.MutableLiveData
import com.example.storyapp.remote.response.LoginResponse
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
class LoginViewModelTest {
    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    @Mock
    private lateinit var storyViewModel: LoginViewModel

    @Before
    fun setUp() {
        storyViewModel = mock(LoginViewModel::class.java)
    }

    @Test
    fun `when login message should return the right data and not null`() {
        val expectedLoginMessage = MutableLiveData<String>()
        expectedLoginMessage.value = "Login Successfully"

        `when`(storyViewModel.message).thenReturn(expectedLoginMessage)
        val actualMessage = storyViewModel.message.getOrAwaitValue()
        verify(storyViewModel).message

        assertNotNull(actualMessage)
        assertEquals(expectedLoginMessage.value, actualMessage)
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
    fun `when login should return the right login user data and not null`() {
        val dummyResponselogin = DataDummy.generateDummyResponseLogin()

        val expectedLogin = MutableLiveData<LoginResponse>()
        expectedLogin.value = dummyResponselogin

        `when`(storyViewModel.userLogin).thenReturn(expectedLogin)

        val actualLoginResponse = storyViewModel.userLogin.getOrAwaitValue()

        verify(storyViewModel).userLogin
        assertNotNull(actualLoginResponse)
        assertEquals(expectedLogin.value, actualLoginResponse)
    }

    @Test
    fun `verify getLoginResponse function is working`() {
        val dummyRequestLogin = DataDummy.generateDummyRequestLogin()
        val dummyResponseLogin = DataDummy.generateDummyResponseLogin()

        val expectedResponseLogin = MutableLiveData<LoginResponse>()
        expectedResponseLogin.value = dummyResponseLogin

        storyViewModel.getLoginResponse(dummyRequestLogin)

        verify(storyViewModel).getLoginResponse(dummyRequestLogin)

        `when`(storyViewModel.userLogin).thenReturn(expectedResponseLogin)

        val actualData = storyViewModel.userLogin.getOrAwaitValue()

        verify(storyViewModel).userLogin
        assertNotNull(expectedResponseLogin)
        assertEquals(expectedResponseLogin.value, actualData)
    }
}