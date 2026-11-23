package com.seuapp.cycletrack.data.remote

import android.content.Context
import com.seuapp.cycletrack.data.local.datastore.PreferencesManager
import com.seuapp.cycletrack.data.remote.api.CycleTrackApi
import com.seuapp.cycletrack.data.remote.interceptors.AppStateInterceptor
import com.seuapp.cycletrack.data.remote.interceptors.AuthInterceptor
import com.seuapp.cycletrack.data.remote.interceptors.CacheInterceptor
import com.seuapp.cycletrack.data.remote.interceptors.GzipRequestInterceptor
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.Cache
import okhttp3.CertificatePinner
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.json.JSONArray
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import java.io.File
import java.util.concurrent.TimeUnit
import javax.inject.Named
import javax.inject.Singleton

/**
 * Módulo Hilt para configuração de rede.
 * Clean-room - Retrofit + OkHttp com interceptors personalizados.
 */
@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Provides
    @Singleton
    fun provideMoshi(): Moshi {
        return Moshi.Builder()
            .add(KotlinJsonAdapterFactory())
            .build()
    }

    @Provides
    @Singleton
    fun provideOkHttpClient(
        @ApplicationContext context: Context,
        authInterceptor: AuthInterceptor,
        loggingInterceptor: HttpLoggingInterceptor,
        cacheInterceptor: CacheInterceptor,
        gzipInterceptor: GzipRequestInterceptor,
        appStateInterceptor: AppStateInterceptor,
        certificatePinner: CertificatePinner
    ): OkHttpClient {
        val cacheDir = File(context.cacheDir, "http_cache")
        val cache = Cache(cacheDir, 50 * 1024 * 1024) // 50MB

        return OkHttpClient.Builder()
            .cache(cache)
            .addInterceptor(authInterceptor)
            .addInterceptor(cacheInterceptor)
            .addInterceptor(gzipInterceptor)
            .addInterceptor(appStateInterceptor)
            .addNetworkInterceptor(loggingInterceptor)
            .certificatePinner(certificatePinner)
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .retryOnConnectionFailure(true)
            .build()
    }

    @Provides
    @Singleton
    fun provideLoggingInterceptor(): HttpLoggingInterceptor {
        return HttpLoggingInterceptor().apply {
            level = if (BuildConfig.DEBUG) HttpLoggingInterceptor.Level.BODY else HttpLoggingInterceptor.Level.NONE
        }
    }

    @Provides
    @Singleton
    fun provideCertificatePinner(
        preferencesManager: PreferencesManager
    ): CertificatePinner {
        val builder = CertificatePinner.Builder()

        // Pinos configurados remotamente: formato "hostname|sha256/BASE64"
        val pinsJson = preferencesManager.getCertificatePins()
        if (!pinsJson.isNullOrBlank()) {
            try {
                val array = JSONArray(pinsJson)
                for (i in 0 until array.length()) {
                    val pin = array.optString(i)
                    val parts = pin.split("|")
                    if (parts.size == 2) {
                        builder.add(parts[0], parts[1])
                    }
                }
            } catch (e: Exception) {
                // Ignora pinos inválidos (configuração remota corrompida)
            }
        }

        return builder.build()
    }

    @Provides
    @Singleton
    fun provideRetrofit(
        okHttpClient: OkHttpClient,
        moshi: Moshi,
        @Named("api_base_url") baseUrl: String
    ): Retrofit {
        return Retrofit.Builder()
            .baseUrl(baseUrl)
            .client(okHttpClient)
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .build()
    }

    @Provides
    @Singleton
    fun provideCycleTrackApi(retrofit: Retrofit): CycleTrackApi {
        return retrofit.create(CycleTrackApi::class.java)
    }

    @Provides
    @Singleton
    @Named("api_base_url")
    fun provideApiBaseUrl(preferencesManager: PreferencesManager): String {
        return preferencesManager.getApiBaseUrl() ?: "https://api.seuapp.com/v1/"
    }
}
