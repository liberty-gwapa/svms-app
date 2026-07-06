package com.svms.app.presentation.notification

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.svms.app.data.model.NotificationSvms
import com.svms.app.data.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class NotificationUiState(
    val notifications: List<NotificationSvms> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null,
    val unreadCount: Int = 0
)

@HiltViewModel
class NotificationViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(NotificationUiState())
    val uiState = _uiState.asStateFlow()

    init {
        fetchNotifications()
    }

    fun fetchNotifications() {
        if (_uiState.value.isLoading) return

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            authRepository.getNotifications()
                .onSuccess { notifications ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        notifications = notifications,
                        unreadCount = notifications.count { !it.isRead }
                    )
                }
                .onFailure { e ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        error = e.message ?: "Failed to load notifications"
                    )
                }
        }
    }

    fun markAsRead(notificationId: String) {
        viewModelScope.launch {
            authRepository.markNotificationAsRead(notificationId)
                .onSuccess {
                    val updatedList = _uiState.value.notifications.map {
                        if (it.notificationId == notificationId) it.copy(isRead = true) else it
                    }
                    _uiState.value = _uiState.value.copy(
                        notifications = updatedList,
                        unreadCount = updatedList.count { !it.isRead }
                    )
                }
        }
    }
}
