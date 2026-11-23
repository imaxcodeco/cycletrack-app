package com.seuapp.cycletrack.di

import android.content.Context
import com.seuapp.cycletrack.data.local.realm.RealmRepository
import com.seuapp.cycletrack.data.local.realm.dao.*
import com.seuapp.cycletrack.data.repository.*
import com.seuapp.cycletrack.domain.repository.*
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import io.realm.Realm
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import timber.log.Timber
import javax.inject.Named
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RealmModule {

    @Provides
    @Singleton
    fun provideRealmRepository(@ApplicationContext context: Context): RealmRepository {
        return RealmRepository.getInstance()
    }
}

@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {

    @Provides
    @Singleton
    fun provideCycleRepository(cycleDao: CycleDao): CycleRepository {
        return CycleRepositoryImpl(cycleDao)
    }

    @Provides
    @Singleton
    fun provideSymptomRepository(symptomDao: SymptomDao): SymptomRepository {
        return SymptomRepositoryImpl(symptomDao)
    }

    @Provides
    @Singleton
    fun providePredictionRepository(): PredictionRepository {
        return PredictionRepositoryImpl()
    }

    @Provides
    @Singleton
    fun provideUserProfileRepository(userProfileDao: UserProfileDao): UserProfileRepository {
        return UserProfileRepositoryImpl(userProfileDao)
    }

    @Provides
    @Singleton
    fun providePregnancyRepository(): PregnancyRepository {
        return PregnancyRepositoryImpl()
    }

    @Provides
    @Singleton
    fun providePartnerRepository(partnerDao: PartnerDao): PartnerRepository {
        return PartnerRepositoryImpl(partnerDao)
    }

    @Provides
    @Singleton
    fun provideSubscriptionRepository(subscriptionDao: SubscriptionDao): SubscriptionRepository {
        return SubscriptionRepositoryImpl(subscriptionDao)
    }

    @Provides
    @Singleton
    fun provideContentRepository(contentDao: ContentDao): ContentRepository {
        return ContentRepositoryImpl(contentDao)
    }

    @Provides
    @Singleton
    fun provideHealthDataPointRepository(healthDataPointDao: HealthDataPointDao): HealthDataPointRepository {
        return HealthDataPointRepositoryImpl(healthDataPointDao)
    }

    @Provides
    @Singleton
    fun provideNotificationRepository(notificationDao: NotificationDao): NotificationRepository {
        return NotificationRepositoryImpl(notificationDao)
    }

    @Provides
    @Singleton
    fun provideExportRepository(exportDao: ExportDao): ExportRepository {
        return ExportRepositoryImpl(exportDao)
    }
}

@Module
@InstallIn(SingletonComponent::class)
object CoroutineModule {

    @Provides
    @Singleton
    @Named("io_dispatcher")
    fun provideIoDispatcher(): CoroutineDispatcher = Dispatchers.IO

    @Provides
    @Singleton
    @Named("default_dispatcher")
    fun provideDefaultDispatcher(): CoroutineDispatcher = Dispatchers.Default

    @Provides
    @Singleton
    @Named("main_dispatcher")
    fun provideMainDispatcher(): CoroutineDispatcher = Dispatchers.Main
}
