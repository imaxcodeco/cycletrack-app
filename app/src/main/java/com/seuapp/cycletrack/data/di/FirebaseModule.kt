package com.seuapp.cycletrack.data.di

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.seuapp.cycletrack.data.remote.firebase.FirebaseAuthRepository
import com.seuapp.cycletrack.domain.repository.AuthRepository
import com.seuapp.cycletrack.domain.usecase.GetCurrentUserUseCase
import com.seuapp.cycletrack.domain.usecase.SignInUseCase
import com.seuapp.cycletrack.domain.usecase.SignOutUseCase
import com.seuapp.cycletrack.domain.usecase.SignUpUseCase
import com.seuapp.cycletrack.domain.usecase.impl.GetCurrentUserUseCaseImpl
import com.seuapp.cycletrack.domain.usecase.impl.SignInUseCaseImpl
import com.seuapp.cycletrack.domain.usecase.impl.SignOutUseCaseImpl
import com.seuapp.cycletrack.domain.usecase.impl.SignUpUseCaseImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object FirebaseModule {

    @Provides
    @Singleton
    fun provideFirebaseAuth(): FirebaseAuth = Firebase.auth

    @Provides
    @Singleton
    fun provideAuthRepository(
        firebaseAuth: FirebaseAuth,
    ): AuthRepository = FirebaseAuthRepository(firebaseAuth)

    @Provides
    @Singleton
    fun provideSignUpUseCase(
        authRepository: AuthRepository,
    ): SignUpUseCase = SignUpUseCaseImpl(authRepository)

    @Provides
    @Singleton
    fun provideSignInUseCase(
        authRepository: AuthRepository,
    ): SignInUseCase = SignInUseCaseImpl(authRepository)

    @Provides
    @Singleton
    fun provideSignOutUseCase(
        authRepository: AuthRepository,
    ): SignOutUseCase = SignOutUseCaseImpl(authRepository)

    @Provides
    @Singleton
    fun provideGetCurrentUserUseCase(
        authRepository: AuthRepository,
    ): GetCurrentUserUseCase = GetCurrentUserUseCaseImpl(authRepository)
}
