package com.svms.app.presentation.violation.details

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.svms.app.data.model.Violation
import com.svms.app.data.repository.ViolationRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class ViolationDetailsUiState(
    val violation: Violation? = null,
    val isLoading: Boolean = false,
    val error: String? = null
)

@HiltViewModel
class ViolationDetailsViewModel @Inject constructor(
    private val violationRepository: ViolationRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val _uiState = MutableStateFlow(ViolationDetailsUiState())
    val uiState = _uiState.asStateFlow()

    private val violationId: String? = savedStateHandle["violationId"]

    init {
        violationId?.let { fetchViolationDetails(it) }
    }

    private fun fetchViolationDetails(id: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            violationRepository.getViolationById(id)
                .onSuccess { violation ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        violation = violation
                    )
                }
                .onFailure { e ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        error = e.message ?: "Failed to load violation details"
                    )
                }
        }
    }
}
