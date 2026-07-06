package com.svms.app.presentation.violation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import android.util.Log
import com.svms.app.data.model.*
import com.svms.app.data.repository.AuthRepository
import com.svms.app.data.repository.ViolationRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

import com.svms.app.data.repository.CloudinaryRepository
import android.content.Context
import android.net.Uri

data class ViolationUiState(
    val isLoading: Boolean = false,
    val isSuccess: Boolean = false,
    val error: String? = null,
    val minorViolations: List<ViolationTypeItem> = emptyList(),
    val majorViolations: List<ViolationTypeItem> = emptyList(),
    val evidenceImageUrl: String? = null,
    val isUploadingImage: Boolean = false
)

@HiltViewModel
class ViolationViewModel @Inject constructor(
    private val violationRepository: ViolationRepository,
    private val authRepository: AuthRepository,
    private val cloudinaryRepository: CloudinaryRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(ViolationUiState())
    val uiState = _uiState.asStateFlow()

    init {
        fetchViolationTypes()
    }

    private fun fetchViolationTypes() {
        viewModelScope.launch {
            violationRepository.getViolationTypes()
                .onSuccess { types ->
                    val items = types.map { type ->
                        ViolationTypeItem(
                            id = type.violationTypeId,
                            name = type.name,
                            category = if (type.severity <= 1) ViolationCategory.MINOR else ViolationCategory.MAJOR
                        )
                    }
                    _uiState.value = _uiState.value.copy(
                        minorViolations = items.filter { it.category == ViolationCategory.MINOR },
                        majorViolations = items.filter { it.category == ViolationCategory.MAJOR }
                    )
                }
        }
    }

    fun uploadEvidenceImage(context: Context, uri: Uri) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isUploadingImage = true, error = null)
            Log.d("SVMS_DEBUG", "ViewModel: Initiating upload for $uri")
            
            val url = cloudinaryRepository.uploadImageToCloudinary(context, uri)
            
            if (!url.isNullOrBlank()) {
                Log.i("SVMS_DEBUG", "ViewModel: Upload successful, URL: $url")
                _uiState.value = _uiState.value.copy(
                    isUploadingImage = false, 
                    evidenceImageUrl = url 
                )
            } else {
                Log.e("SVMS_DEBUG", "ViewModel: Upload returned null URL")
                _uiState.value = _uiState.value.copy(
                    isUploadingImage = false, 
                    error = "Failed to upload image. Please try again."
                )
            }
        }
    }

    fun removeEvidenceImage() {
        _uiState.value = _uiState.value.copy(evidenceImageUrl = null)
    }

    fun submitViolation(
        student: Student?,
        violationTypeItem: ViolationTypeItem?,
        remarks: String,
        location: String
    ) {
        if (student == null) {
            _uiState.value = _uiState.value.copy(error = "Please identify a student first")
            return
        }
        
        if (student.schoolId == 0L) {
            _uiState.value = _uiState.value.copy(error = "Selected student has an invalid School ID.")
            return
        }

        if (violationTypeItem == null) {
            _uiState.value = _uiState.value.copy(error = "Please select a violation type")
            return
        }

        val currentUser = authRepository.currentUser.value
        if (currentUser == null) {
            _uiState.value = _uiState.value.copy(error = "You must be logged in to report a violation")
            return
        }

        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.US)
        val timeFormat = SimpleDateFormat("HH:mm:ss", Locale.US)
        val now = Date()

        val violation = Violation(
            violationId = UUID.randomUUID().toString(),
            studentId = student.schoolId,
            violationTypeId = violationTypeItem.id,
            reportedByGuard = currentUser.userId,
            violationDate = dateFormat.format(now),
            violationTime = timeFormat.format(now),
            description = remarks.ifBlank { "No additional remarks provided." },
            place = location.ifBlank { null },
            status = "PENDING",
            evidenceUrl = _uiState.value.evidenceImageUrl // Explicitly pass the URL from state
        )
        
        Log.i("SVMS_DEBUG", "SUBMITTING VIOLATION: URL=${violation.evidenceUrl}")

        violation.apply {
            this.studentName = student.fullName
            this.violationType = violationTypeItem.name
            this.violationCategory = violationTypeItem.category
            this.guardName = currentUser.fullName
            this.createdAt = now.time
        }

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            
            violationRepository.addViolation(violation)
                .onSuccess {
                    Log.i("SVMS_DEBUG", "Submission Success")
                    _uiState.value = _uiState.value.copy(isLoading = false, isSuccess = true, evidenceImageUrl = null)
                }
                .onFailure { e ->
                    Log.e("SVMS_DEBUG", "Submission Failure", e)
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        error = e.message ?: "Failed to submit violation"
                    )
                }
        }
    }

    fun resetSuccess() {
        _uiState.value = _uiState.value.copy(isSuccess = false, error = null, evidenceImageUrl = null)
    }
}
