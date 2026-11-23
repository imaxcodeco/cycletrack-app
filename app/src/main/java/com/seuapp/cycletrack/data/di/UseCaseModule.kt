package com.seuapp.cycletrack.data.di

import com.seuapp.cycletrack.domain.repository.CycleRepository
import com.seuapp.cycletrack.domain.repository.SymptomRepository
import com.seuapp.cycletrack.domain.usecase.CalculateCycleDayUseCase
import com.seuapp.cycletrack.domain.usecase.CalculateFertileWindowUseCase
import com.seuapp.cycletrack.domain.usecase.CalculateOvulationDateUseCase
import com.seuapp.cycletrack.domain.usecase.GetCurrentCyclePhaseUseCase
import com.seuapp.cycletrack.domain.usecase.GetCycleStatsUseCase
import com.seuapp.cycletrack.domain.usecase.GetSymptomHistoryUseCase
import com.seuapp.cycletrack.domain.usecase.GetSymptomInsightsUseCase
import com.seuapp.cycletrack.domain.usecase.LogSymptomUseCase
import com.seuapp.cycletrack.domain.usecase.PredictNextPeriodUseCase
import com.seuapp.cycletrack.domain.usecase.impl.CalculateCycleDayUseCaseImpl
import com.seuapp.cycletrack.domain.usecase.impl.CalculateFertileWindowUseCaseImpl
import com.seuapp.cycletrack.domain.usecase.impl.CalculateOvulationDateUseCaseImpl
import com.seuapp.cycletrack.domain.usecase.impl.GetCurrentCyclePhaseUseCaseImpl
import com.seuapp.cycletrack.domain.usecase.impl.GetCycleStatsUseCaseImpl
import com.seuapp.cycletrack.domain.usecase.impl.GetSymptomHistoryUseCaseImpl
import com.seuapp.cycletrack.domain.usecase.impl.GetSymptomInsightsUseCaseImpl
import com.seuapp.cycletrack.domain.usecase.impl.LogSymptomUseCaseImpl
import com.seuapp.cycletrack.domain.usecase.impl.PredictNextPeriodUseCaseImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object UseCaseModule {

    @Provides
    @Singleton
    fun provideGetCurrentCyclePhaseUseCase(
        cycleRepository: CycleRepository
    ): GetCurrentCyclePhaseUseCase = GetCurrentCyclePhaseUseCaseImpl(cycleRepository)

    @Provides
    @Singleton
    fun provideCalculateCycleDayUseCase(
        cycleRepository: CycleRepository
    ): CalculateCycleDayUseCase = CalculateCycleDayUseCaseImpl(cycleRepository)

    @Provides
    @Singleton
    fun providePredictNextPeriodUseCase(
        cycleRepository: CycleRepository
    ): PredictNextPeriodUseCase = PredictNextPeriodUseCaseImpl(cycleRepository)

    @Provides
    @Singleton
    fun provideCalculateFertileWindowUseCase(
        cycleRepository: CycleRepository
    ): CalculateFertileWindowUseCase = CalculateFertileWindowUseCaseImpl(cycleRepository)

    @Provides
    @Singleton
    fun provideCalculateOvulationDateUseCase(
        cycleRepository: CycleRepository
    ): CalculateOvulationDateUseCase = CalculateOvulationDateUseCaseImpl(cycleRepository)

    @Provides
    @Singleton
    fun provideGetCycleStatsUseCase(
        cycleRepository: CycleRepository
    ): GetCycleStatsUseCase = GetCycleStatsUseCaseImpl(cycleRepository)

    @Provides
    @Singleton
    fun provideLogSymptomUseCase(
        symptomRepository: SymptomRepository
    ): LogSymptomUseCase = LogSymptomUseCaseImpl(symptomRepository)

    @Provides
    @Singleton
    fun provideGetSymptomInsightsUseCase(
        symptomRepository: SymptomRepository
    ): GetSymptomInsightsUseCase = GetSymptomInsightsUseCaseImpl(symptomRepository)

    @Provides
    @Singleton
    fun provideGetSymptomHistoryUseCase(
        symptomRepository: SymptomRepository
    ): GetSymptomHistoryUseCase = GetSymptomHistoryUseCaseImpl(symptomRepository)
}
