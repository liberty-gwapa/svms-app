package com.svms.app.presentation.scanner

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.svms.app.data.model.Student
import com.svms.app.data.repository.StudentRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class ScannerUiState(
    val searchQuery: String = "",
    val isScanning: Boolean = false,
    val isLoading: Boolean = false,
    val scannedStudent: Student? = null,
    val searchResults: List<Student> = emptyList(),
    val error: String? = null,
    val navigateToViolation: Boolean = false
)

@HiltViewModel
class ScannerViewModel @Inject constructor(
    private val studentRepository: StudentRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(ScannerUiState())
    val uiState = _uiState.asStateFlow()

    private var searchJob: Job? = null

    fun onSearchQueryChange(query: String) {
        _uiState.value = _uiState.value.copy(searchQuery = query, error = null)
        
        searchJob?.cancel()
        if (query.length >= 2) {
            searchJob = viewModelScope.launch {
                delay(300) // Debounce for 300ms
                searchStudents(query)
            }
        } else {
            _uiState.value = _uiState.value.copy(searchResults = emptyList())
        }
    }

    fun onBarcodeScanned(barcode: String) {
        _uiState.value = _uiState.value.copy(isScanning = false)
        fetchStudent(barcode)
    }

    fun onManualSearch() {
        val query = _uiState.value.searchQuery.trim()
        if (query.isBlank()) {
            _uiState.value = _uiState.value.copy(error = "Please enter a Student ID or Name")
            return
        }
        fetchStudent(query)
    }

    fun onStudentSelected(student: Student) {
        _uiState.value = _uiState.value.copy(
            scannedStudent = student,
            searchResults = emptyList(),
            searchQuery = student.schoolId.toString(),
            navigateToViolation = true
        )
    }

    fun resetNavigation() {
        _uiState.value = _uiState.value.copy(navigateToViolation = false, scannedStudent = null)
    }

    fun startScanning() {
        _uiState.value = _uiState.value.copy(isScanning = true, error = null)
    }

    fun stopScanning() {
        _uiState.value = _uiState.value.copy(isScanning = false)
    }

    private fun fetchStudent(id: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            studentRepository.getStudentById(id)
                .onSuccess { student ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        scannedStudent = student,
                        navigateToViolation = true,
                        searchResults = emptyList()
                    )
                }
                .onFailure { e ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        error = e.message ?: "Student not found"
                    )
                }
        }
    }

    private fun searchStudents(query: String) {
        viewModelScope.launch {
            studentRepository.searchStudents(query)
                .onSuccess { students ->
                    _uiState.value = _uiState.value.copy(searchResults = students)
                }
        }
    }
}
