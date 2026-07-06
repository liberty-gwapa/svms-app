package com.svms.app.presentation.splash

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.svms.app.data.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import android.util.Log
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SplashViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _isReady = MutableStateFlow(false)
    val isReady = _isReady.asStateFlow()

    private val _isLoggedIn = MutableStateFlow(false)
    val isLoggedIn = _isLoggedIn.asStateFlow()

    init {
        checkSession()
    }

    private fun checkSession() {
        Log.d("SVMS_AUTH", "SplashViewModel: checkSession() started")
        viewModelScope.launch {
            val loggedIn = authRepository.initialize()
            Log.d("SVMS_AUTH", "SplashViewModel: isLoggedIn check result = $loggedIn")

            _isLoggedIn.value = loggedIn
            delay(1500) // Minimum splash duration
            _isReady.value = true
            Log.d("SVMS_AUTH", "SplashViewModel: Ready to navigate")
        }
    }
}
