package com.yangmaolie.hunter.presentation.ui.deal_detail

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.yangmaolie.hunter.core.base.BaseViewModel
import com.yangmaolie.hunter.data.remote.firebase.AuthService
import com.yangmaolie.hunter.data.remote.firebase.FirestoreService
import com.yangmaolie.hunter.domain.model.ActionType
import com.yangmaolie.hunter.domain.model.Deal
import com.yangmaolie.hunter.domain.usecase.recommend.LogUserBehaviorUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DealDetailViewModel @Inject constructor(
    private val authService: AuthService,
    private val firestoreService: FirestoreService,
    private val logUserBehaviorUseCase: LogUserBehaviorUseCase
) : BaseViewModel() {

    private val _deal = MutableLiveData<Deal?>()
    val deal: LiveData<Deal?> = _deal

    private val _isCollected = MutableLiveData<Boolean>()
    val isCollected: LiveData<Boolean> = _isCollected

    private var startTime: Long = 0L

    fun loadDeal(dealId: String) {
        startTime = System.currentTimeMillis()
        launchWithLoading {
            val deal = firestoreService.getDealById(dealId)
            _deal.postValue(deal)

            val userId = authService.getCurrentUserId() ?: return@launchWithLoading
            val isCollected = firestoreService.isCollected(userId, dealId)
            _isCollected.postValue(isCollected)

            // Log view
            deal?.let {
                logUserBehaviorUseCase(
                    userId = userId,
                    dealId = dealId,
                    category = it.category,
                    actionType = ActionType.VIEW,
                    durationSeconds = 0
                )
            }
        }
    }

    fun toggleCollection() {
        val deal = _deal.value ?: return
        val userId = authService.getCurrentUserId() ?: return

        launchWithLoading {
            val currentlyCollected = _isCollected.value ?: false
            if (currentlyCollected) {
                firestoreService.removeFromCollection(userId, deal.dealId)
                _isCollected.postValue(false)
            } else {
                firestoreService.addToCollection(userId, deal.dealId)
                _isCollected.postValue(true)
                // Log collect action
                logUserBehaviorUseCase(
                    userId = userId,
                    dealId = deal.dealId,
                    category = deal.category,
                    actionType = ActionType.COLLECT
                )
            }
        }
    }

    fun onDislike() {
        val deal = _deal.value ?: return
        val userId = authService.getCurrentUserId() ?: return

        launchWithLoading {
            logUserBehaviorUseCase(
                userId = userId,
                dealId = deal.dealId,
                category = deal.category,
                actionType = ActionType.DISLIKE
            )
        }
    }

    fun onPageExit() {
        val deal = _deal.value ?: return
        val userId = authService.getCurrentUserId() ?: return
        val duration = ((System.currentTimeMillis() - startTime) / 1000).toInt()

        if (duration >= 30) {
            launchWithLoading {
                logUserBehaviorUseCase(
                    userId = userId,
                    dealId = deal.dealId,
                    category = deal.category,
                    actionType = ActionType.VIEW_LONG,
                    durationSeconds = duration
                )
            }
        }
    }
}
