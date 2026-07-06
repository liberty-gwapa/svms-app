package com.svms.app.presentation.history

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.svms.app.data.model.User
import com.svms.app.data.model.Violation
import com.svms.app.data.repository.AuthRepository
import com.svms.app.data.repository.ViolationRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class HistoryUiState(
    val violations: List<Violation> = emptyList(),
    val totalIncidents: Int = 0,
    val pendingReview: Int = 0,
    val isLoading: Boolean = false,
    val error: String? = null,
    val currentUser: User? = null
)

@HiltViewModel
class HistoryViewModel @Inject constructor(
    private val violationRepository: ViolationRepository,
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(HistoryUiState())
    val uiState = _uiState.asStateFlow()

    init {
        // Observe the flow in repository
        viewModelScope.launch {
            violationRepository.violations.collect { violations ->
                _uiState.value = _uiState.value.copy(
                    violations = violations,
                    totalIncidents = violations.size,
                    pendingReview = violationRepository.getPendingReviewCount()
                )
            }
        }
        
        observeCurrentUser()
        refreshHistory()
    }

    private fun observeCurrentUser() {
        viewModelScope.launch {
            authRepository.currentUser.collect { user ->
                _uiState.value = _uiState.value.copy(currentUser = user)
            }
        }
    }

    fun refreshHistory() {
        if (_uiState.value.isLoading) return

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            violationRepository.fetchTodayViolations()
                .onFailure { e ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false, 
                        error = e.message ?: "Failed to load history"
                    )
                }
                .onSuccess {
                    _uiState.value = _uiState.value.copy(isLoading = false)
                }
        }
    }
}
