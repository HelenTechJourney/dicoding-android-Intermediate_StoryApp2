package com.example.storyapp.ui.viewmodel

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.MutableLiveData
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
class DataStoreViewModelTest {
    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    @Mock
    private lateinit var storyViewModel: DataStoreViewModel

    private val token = "test token"
    private val name = "test nama"
    private val loginState = true

    @Before
    fun setUp() {
        storyViewModel = mock(DataStoreViewModel::class.java)
    }

    @Test
    fun `when getLoginState return the right data and not null`() {
        val expectedLoginState = MutableLiveData<Boolean>()
        expectedLoginState.value = loginState

        `when`(storyViewModel.getLoginState()).thenReturn(expectedLoginState)

        val actualLoginState = storyViewModel.getLoginState().getOrAwaitValue()

        verify(storyViewModel).getLoginState()
        assertNotNull(actualLoginState)
        assertEquals(expectedLoginState.value, actualLoginState)
    }

    @Test
    fun `verify saveLoginState function is working`() {
        storyViewModel.saveLoginState(loginState)
        verify(storyViewModel).saveLoginState(loginState)
    }

    @Test
    fun `when getToken return the right data and not null`() {
        val expectedToken = MutableLiveData<String>()
        expectedToken.value = token

        `when`(storyViewModel.getToken()).thenReturn(expectedToken)

        val actualToken = storyViewModel.getToken().getOrAwaitValue()

        verify(storyViewModel).getToken()
        assertNotNull(actualToken)
        assertEquals(expectedToken.value, actualToken)
    }

    @Test
    fun `verify saveToken function is working`() {
        val token = "test token"

        storyViewModel.saveToken(token)
        verify(storyViewModel).saveToken(token)
    }

    @Test
    fun `verify saveName function is working`() {
        storyViewModel.saveName(name)
        verify(storyViewModel).saveName(name)
    }
}