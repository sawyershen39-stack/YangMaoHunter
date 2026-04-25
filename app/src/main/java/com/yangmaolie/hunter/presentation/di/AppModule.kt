package com.yangmaolie.hunter.presentation.di

import com.yangmaolie.hunter.data.remote.firebase.AuthService
import com.yangmaolie.hunter.data.remote.firebase.FirestoreService
import com.yangmaolie.hunter.data.repository.DealRepositoryImpl
import com.yangmaolie.hunter.data.repository.OfflineDealRepository
import com.yangmaolie.hunter.data.repository.OfflineRecommendRepository
import com.yangmaolie.hunter.data.repository.RecommendRepositoryImpl
import com.yangmaolie.hunter.domain.repository.DealRepository
import com.yangmaolie.hunter.domain.repository.RecommendRepository
import com.yangmaolie.hunter.domain.usecase.recommend.GetRecommendationsUseCase
import com.yangmaolie.hunter.domain.usecase.recommend.LogUserBehaviorUseCase
import com.yangmaolie.hunter.domain.usecase.recommend.UpdateUserPreferencesUseCase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideAuthService(): AuthService {
        return AuthService()
    }

    @Provides
    @Singleton
    fun provideFirestoreService(): FirestoreService {
        return FirestoreService()
    }

    @Provides
    @Singleton
    fun provideDealRepository(
        firestoreService: FirestoreService,
        offlineDealRepository: OfflineDealRepository
    ): DealRepository {
        // If Firestore is not available (offline/China network), use offline repo
        return if (firestoreService.isAvailable()) {
            DealRepositoryImpl(firestoreService)
        } else {
            offlineDealRepository
        }
    }

    @Provides
    @Singleton
    fun provideRecommendRepository(
        firestoreService: FirestoreService,
        offlineRecommendRepository: OfflineRecommendRepository
    ): RecommendRepository {
        // If Firestore is not available (offline/China network), use offline repo
        return if (firestoreService.isAvailable()) {
            RecommendRepositoryImpl(firestoreService)
        } else {
            offlineRecommendRepository
        }
    }

    @Provides
    @Singleton
    fun provideGetRecommendationsUseCase(
        recommendRepository: RecommendRepository,
        dealRepository: DealRepository
    ): GetRecommendationsUseCase {
        return GetRecommendationsUseCase(recommendRepository, dealRepository)
    }

    @Provides
    @Singleton
    fun provideLogUserBehaviorUseCase(
        recommendRepository: RecommendRepository
    ): LogUserBehaviorUseCase {
        return LogUserBehaviorUseCase(recommendRepository)
    }

    @Provides
    @Singleton
    fun provideUpdateUserPreferencesUseCase(
        recommendRepository: RecommendRepository
    ): UpdateUserPreferencesUseCase {
        return UpdateUserPreferencesUseCase(recommendRepository)
    }
}
