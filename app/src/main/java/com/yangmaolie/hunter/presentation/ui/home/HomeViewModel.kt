package com.yangmaolie.hunter.presentation.ui.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.yangmaolie.hunter.core.base.BaseViewModel
import com.yangmaolie.hunter.data.remote.firebase.AuthService
import com.yangmaolie.hunter.domain.model.Deal
import com.yangmaolie.hunter.domain.model.UserPreference
import com.yangmaolie.hunter.domain.repository.RecommendRepository
import com.yangmaolie.hunter.domain.usecase.recommend.GetRecommendationsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val getRecommendationsUseCase: GetRecommendationsUseCase,
    private val recommendRepository: RecommendRepository,
    private val authService: AuthService
) : BaseViewModel() {

    private val _deals = MutableStateFlow<List<Deal>>(emptyList())
    val deals: StateFlow<List<Deal>> = _deals

    private val _isRefreshing = MutableLiveData<Boolean>()
    val isRefreshing: LiveData<Boolean> = _isRefreshing

    private val _shouldShowOnboarding = MutableLiveData<Boolean>()
    val shouldShowOnboarding: LiveData<Boolean> = _shouldShowOnboarding

    private var currentCategory: String? = null

    init {
        loadRecommendations()
    }

    fun loadRecommendations(category: String? = currentCategory) {
        currentCategory = category
        val userId = authService.getCurrentUserId() ?: return

        launchWithLoading {
            val result = getRecommendationsUseCase(userId, category)
            result.onSuccess { deals ->
                _deals.value = deals
            }.onFailure { e ->
                _errorMessage.value = e.message
            }
        }
    }

    fun checkOnboardingStatus() {
        val userId = authService.getCurrentUserId() ?: return
        launchWithLoading {
            val preferences = recommendRepository.getUserPreferences(userId)
            _shouldShowOnboarding.postValue(preferences == null)
        }
    }

    fun onRefresh() {
        _isRefreshing.value = true
        loadRecommendations(currentCategory)
        _isRefreshing.value = false
    }
}
