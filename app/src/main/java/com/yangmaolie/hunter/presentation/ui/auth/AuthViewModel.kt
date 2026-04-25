package com.yangmaolie.hunter.presentation.ui.auth

import androidx.lifecycle.ViewModel
import com.yangmaolie.hunter.data.remote.firebase.AuthService
import com.yangmaolie.hunter.domain.model.UserPreference
import com.yangmaolie.hunter.domain.repository.RecommendRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val authService: AuthService,
    private val recommendRepository: RecommendRepository
) : ViewModel() {

    sealed class AuthState {
        object Loading : AuthState()
        data class Success(val userId: String) : AuthState()
        data class Error(val message: String) : AuthState()
    }

    private val _authState = MutableStateFlow<AuthState>(AuthState.Loading)
    val authState: StateFlow<AuthState> = _authState

    suspend fun anonymousSignIn() {
        _authState.value = AuthState.Loading
        val result = authService.signInAnonymously()
        result.onSuccess { userId ->
            // Check if preferences exist, create default if not
            val existing = recommendRepository.getUserPreferences(userId)
            if (existing == null) {
                val defaultPrefs = UserPreference.createDefault(userId)
                recommendRepository.saveUserPreferences(defaultPrefs)
            }
            _authState.value = AuthState.Success(userId)
        }.onFailure { e ->
            _authState.value = AuthState.Error(e.message ?: "登录失败")
        }
    }
}
