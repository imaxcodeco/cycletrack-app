package com.seuapp.cycletrack.di

import android.content.Context
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.seuapp.cycletrack.data.local.datastore.PreferencesManager
import com.seuapp.cycletrack.data.local.realm.RealmRepository
import com.seuapp.cycletrack.data.local.room.AppDatabase
import com.seuapp.cycletrack.domain.usecase.subscription.BillingClientWrapper
import com.seuapp.cycletrack.domain.usecase.subscription.StubBillingClientWrapper
import com.seuapp.cycletrack.util.AppLogger
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityComponent
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.android.scopes.ActivityScoped
import dagger.hilt.components.SingletonComponent
import io.realm.Realm
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import javax.inject.Named
import javax.inject.Singleton

/**
 * Módulos Hilt para injeção de dependência Android.
 * Clean-room - fornece implementações Android para interfaces de domínio.
 */

@Module
@InstallIn(SingletonComponent::class)
object ApplicationModule {

    @Provides
    @Singleton
    fun provideRealmRepository(@ApplicationContext context: Context): RealmRepository {
        Realm.init(context)
        return RealmRepository.getInstance()
    }

    @Provides
    @Singleton
    fun provideAppDatabase(@ApplicationContext context: Context): AppDatabase {
        return AppDatabase.getInstance(context)
    }

    @Provides
    @Singleton
    fun providePreferencesManager(@ApplicationContext context: Context): PreferencesManager {
        return PreferencesManager.getInstance(context)
    }

    @Provides
    @Singleton
    fun provideAppLogger(@ApplicationContext context: Context): AppLogger {
        return AppLogger.init(context)
    }

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
    fun provideGoogleSignInOptions(@ApplicationContext context: Context): GoogleSignInOptions {
        return GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestEmail()
            .requestProfile()
            .requestIdToken(context.getString(R.string.default_web_client_id))
            .build()
    }
}

@Module
@InstallIn(ActivityComponent::class)
object ActivityModule {

    @Provides
    @ActivityScoped
    fun provideGoogleSignInClient(
        @ApplicationContext context: Context,
        options: GoogleSignInOptions
    ) = GoogleSignIn.getClient(context, options)
}

@Module
@InstallIn(SingletonComponent::class)
object BillingModule {

    @Provides
    @Singleton
    fun provideBillingClientWrapper(
        @ApplicationContext context: Context,
        preferencesManager: PreferencesManager
    ): BillingClientWrapper {
        return StubBillingClientWrapper(context, preferencesManager)
    }
}
