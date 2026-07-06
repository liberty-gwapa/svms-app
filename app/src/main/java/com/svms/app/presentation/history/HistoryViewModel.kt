package com.svms.app.presentation.history

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.svms.app.data.model.Department
import com.svms.app.data.model.User
import com.svms.app.data.model.Violation
import com.svms.app.data.repository.AuthRepository
import com.svms.app.data.repository.ViolationRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

enum class HistoryFilter {
    TODAY,
    ALL_TIME
}

data class HistoryUiState(
    val violations: List<Violation> = emptyList(),
    val filteredViolations: List<Violation> = emptyList(),
    val totalIncidents: Int = 0,
    val todayReportsCount: Int = 0,
    val isLoading: Boolean = false,
    val error: String? = null,
    val currentUser: User? = null,
    val currentFilter: HistoryFilter = HistoryFilter.TODAY,
    val departments: List<Department> = emptyList(),
    val selectedDepartment: String? = null
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
                _uiState.update { 
                    it.copy(
                        violations = violations,
                        todayReportsCount = violations.size
                    )
                }
                applyFilter()
            }
        }
        
        observeCurrentUser()
        fetchDepartments()
        refreshHistory()
        fetchAllTimeIncidents()
    }

    private fun fetchDepartments() {
        viewModelScope.launch {
            android.util.Log.d("SVMS_DEBUG", "ViewModel: Calling getDepartments()")
            violationRepository.getDepartments()
                .onSuccess { depts ->
                    android.util.Log.d("SVMS_DEBUG", "ViewModel: Received ${depts.size} departments from repository")
                    depts.forEach { 
                        android.util.Log.d("SVMS_DEBUG", "Found Department: ${it.departmentKey}")
                    }
                    _uiState.update { it.copy(departments = depts) }
                }
                .onFailure { e ->
                    android.util.Log.e("SVMS_DEBUG", "ViewModel: Failed to fetch departments", e)
                }
        }
    }

    private fun observeCurrentUser() {
        viewModelScope.launch {
            authRepository.currentUser.collect { user ->
                _uiState.update { it.copy(currentUser = user) }
                if (user != null) {
                    fetchAllTimeIncidents()
                }
            }
        }
    }

    fun setFilter(filter: HistoryFilter) {
        _uiState.update { it.copy(currentFilter = filter) }
        applyFilter()
        if (filter == HistoryFilter.ALL_TIME) {
            fetchAllTimeIncidents()
        } else {
            refreshHistory()
        }
    }

    fun setSelectedDepartment(deptKey: String?) {
        _uiState.update { it.copy(selectedDepartment = deptKey) }
        applyFilter()
    }

    private fun applyFilter() {
        _uiState.update { state ->
            val baseList = if (state.currentFilter == HistoryFilter.TODAY) {
                state.violations
            } else {
                state.violations // This will be updated by fetchAllTimeIncidents or we should use a different storage
                // Actually, refreshHistory and fetchAllTimeIncidents update state.violations and filteredViolations
                // Let's refine this to make it more robust.
                state.violations 
            }
            
            val filtered = baseList.filter { violation ->
                state.selectedDepartment == null || 
                violation.studentCourse?.equals(state.selectedDepartment, ignoreCase = true) == true
            }
            state.copy(filteredViolations = filtered)
        }
    }

    fun refreshHistory() {
        if (_uiState.value.isLoading) return

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            violationRepository.fetchTodayViolations()
                .onFailure { e ->
                    _uiState.update { 
                        it.copy(
                            isLoading = false, 
                            error = e.message ?: "Failed to load history"
                        )
                    }
                }
                .onSuccess { violations ->
                    _uiState.update { state ->
                        val filtered = violations.filter { v ->
                            state.selectedDepartment == null || 
                            v.studentCourse?.equals(state.selectedDepartment, ignoreCase = true) == true
                        }
                        state.copy(
                            isLoading = false,
                            violations = violations,
                            filteredViolations = filtered,
                            todayReportsCount = violations.size
                        )
                    }
                }
        }
    }

    private fun fetchAllTimeIncidents() {
        val user = _uiState.value.currentUser ?: return
        
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            violationRepository.fetchAllTimeGuardViolations(user.userId)
                .onSuccess { violations ->
                    _uiState.update { state ->
                        val filtered = violations.filter { v ->
                            state.selectedDepartment == null || 
                            v.studentCourse?.equals(state.selectedDepartment, ignoreCase = true) == true
                        }
                        state.copy(
                            isLoading = false,
                            violations = violations,
                            filteredViolations = filtered,
                            totalIncidents = violations.size
                        )
                    }
                }
                .onFailure { e ->
                    _uiState.update { 
                        it.copy(
                            isLoading = false,
                            error = e.message ?: "Failed to load total incidents"
                        )
                    }
                }
        }
    }
}
