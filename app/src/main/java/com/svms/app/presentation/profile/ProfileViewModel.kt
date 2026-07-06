package com.svms.app.presentation.profile

import android.content.Context
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.svms.app.data.model.User
import com.svms.app.data.repository.AuthRepository
import com.svms.app.data.repository.CloudinaryRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

data class ProfileUiState(
    val user: User? = null,
    val isLoading: Boolean = false,
    val error: String? = null,
    val isUploading: Boolean = false
)

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val cloudinaryRepository: CloudinaryRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(ProfileUiState())
    val uiState = _uiState.asStateFlow()

    init {
        observeCurrentUser()
    }

    private fun observeCurrentUser() {
        viewModelScope.launch {
            authRepository.currentUser.collectLatest { user ->
                _uiState.value = _uiState.value.copy(user = user)
            }
        }
    }

    fun uploadProfileImage(context: Context, uri: Uri) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isUploading = true, error = null)
            
            val imageUrl = cloudinaryRepository.uploadImageToCloudinary(context, uri)
            if (imageUrl != null) {
                authRepository.updateProfileImage(imageUrl)
                    .onSuccess {
                        _uiState.value = _uiState.value.copy(isUploading = false)
                    }
                    .onFailure { e ->
                        _uiState.value = _uiState.value.copy(isUploading = false, error = "Failed to update profile: ${e.message}")
                    }
            } else {
                _uiState.value = _uiState.value.copy(isUploading = false, error = "Failed to upload image to Cloudinary")
            }
        }
    }

    fun logout() {
        viewModelScope.launch {
            authRepository.logout()
        }
    }
}
