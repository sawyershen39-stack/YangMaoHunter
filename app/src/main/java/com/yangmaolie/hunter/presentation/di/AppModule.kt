package com.yangmaolie.hunter.presentation.di

import com.yangmaolie.hunter.data.remote.firebase.AuthService
import com.yangmaolie.hunter.data.remote.firebase.FirestoreService
import com.yangmaolie.hunter.data.repository.DealRepositoryImpl
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
        firestoreService: FirestoreService
    ): DealRepository {
        return DealRepositoryImpl(firestoreService)
    }

    @Provides
    @Singleton
    fun provideRecommendRepository(
        firestoreService: FirestoreService
    ): RecommendRepository {
        return RecommendRepositoryImpl(firestoreService)
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
