// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    id("com.android.application") version "8.6.1" apply false
    id("org.jetbrains.kotlin.android") version "1.9.22" apply false
    id("org.jetbrains.kotlin.plugin.serialization") version "1.9.22" apply false
    id("org.jetbrains.kotlin.kapt") version "1.9.22" apply false
    id("org.jetbrains.kotlin.plugin.parcelize") version "1.9.22" apply false
    id("com.google.dagger.hilt.android") version "2.48" apply false
    id("com.google.gms.google-services") version "4.4.1" apply false
    id("realm-android") version "10.19.0" apply false
    id("io.gitlab.arturbosch.detekt") version "1.23.4" apply false
    id("org.jlleitschuh.gradle.ktlint") version "11.6.1" apply false
}

// Repositorios sao declarados centralmente em settings.gradle.kts
// (RepositoriesMode.FAIL_ON_PROJECT_REPOS impede blocos allprojects{repositories})

tasks.register("clean", Delete::class) {
    delete(rootProject.buildDir)
}

// ============ Dependency Versions Catalog ============
// Centralized version management
ext["versions"] = mapOf(
    "agp" to "8.4.0",
    "kotlin" to "1.9.22",
    "compileSdk" to 35,
    "minSdk" to 24,
    "targetSdk" to 35,
    "coreKtx" to "1.13.1",
    "appcompat" to "1.7.0",
    "activity" to "1.9.0",
    "fragment" to "1.8.0",
    "lifecycle" to "2.8.0",
    "savedstate" to "1.2.1",
    "composeBom" to "2024.04.01",
    "composeCompiler" to "1.5.8",
    "navigation" to "2.8.0",
    "hilt" to "2.48",
    "koin" to "3.5.6",
    "coroutines" to "1.8.0",
    "serialization" to "1.6.3",
    "retrofit" to "2.11.0",
    "okhttp" to "4.12.0",
    "moshi" to "1.15.1",
    "room" to "2.6.1",
    "datastore" to "1.1.1",
    "security" to "1.1.0-alpha06",
    "work" to "2.9.0",
    "billing" to "7.0.0",
    "healthConnect" to "1.1.0",
    "firebaseBom" to "33.1.0",
    "biometric" to "1.2.0-alpha04",
    "sentry" to "7.11.0",
    "coil" to "2.6.0",
    "accompanist" to "0.32.0",
    "itext" to "7.2.5",
    "opencsv" to "5.9",
    "junit" to "4.13.2",
    "junitJupiter" to "5.10.2",
    "mockito" to "5.11.0",
    "mockk" to "1.13.12",
    "robolectric" to "4.11",
    "espresso" to "3.5.1",
    "truth" to "1.1.5",
    "detekt" to "1.23.4",
    "ksp" to "1.9.22-1.0.13",
    "protobuf" to "0.9.4",
    "grpc" to "1.62.1",
    "protobufRuntime" to "3.21.12"
)

ext["deps"] = mapOf(
    // Core
    "core-ktx" to "androidx.core:core-ktx:\${versions.coreKtx}",
    "appcompat" to "androidx.appcompat:appcompat:\${versions.appcompat}",
    "activity-compose" to "androidx.activity:activity-compose:\${versions.activity}",
    "activity-ktx" to "androidx.activity:activity-ktx:\${versions.activity}",
    "fragment-ktx" to "androidx.fragment:fragment-ktx:\${versions.fragment}",
    "lifecycle-runtime-ktx" to "androidx.lifecycle:lifecycle-runtime-ktx:\${versions.lifecycle}",
    "lifecycle-viewmodel-compose" to "androidx.lifecycle:lifecycle-viewmodel-compose:\${versions.lifecycle}",
    "lifecycle-livedata-ktx" to "androidx.lifecycle:lifecycle-livedata-ktx:\${versions.lifecycle}",
    "savedstate-ktx" to "androidx.savedstate:savedstate-ktx:\${versions.savedstate}",

    // Compose
    "compose-bom" to "androidx.compose:compose-bom:\${versions.composeBom}",
    "ui" to "androidx.compose.ui:ui",
    "ui-graphics" to "androidx.compose.ui:ui-graphics",
    "ui-tooling-preview" to "androidx.compose.ui:ui-tooling-preview",
    "material3" to "androidx.compose.material3:material3",
    "material3-window-size-class" to "androidx.compose.material3:material3-window-size-class",
    "runtime-livedata" to "androidx.compose.runtime:runtime-livedata",
    "runtime-rxjava3" to "androidx.compose.runtime:runtime-rxjava3",
    "animation" to "androidx.compose.animation:animation",
    "animation-graphics" to "androidx.compose.animation:animation-graphics",
    "foundation" to "androidx.compose.foundation:foundation",
    "foundation-layout" to "androidx.compose.foundation:foundation-layout",

    // Navigation
    "navigation-compose" to "androidx.navigation:navigation-compose:\${versions.navigation}",
    "navigation-fragment-ktx" to "androidx.navigation:navigation-fragment-ktx:\${versions.navigation}",
    "navigation-ui-ktx" to "androidx.navigation:navigation-ui-ktx:\${versions.navigation}",

    // Hilt
    "hilt-android" to "com.google.dagger:hilt-android:\${versions.hilt}",
    "hilt-navigation-compose" to "com.google.dagger:hilt-navigation-compose:1.2.0",
    "hilt-compiler" to "com.google.dagger:hilt-compiler:\${versions.hilt}",
    "hilt-androidx-compiler" to "androidx.hilt:hilt-compiler:1.2.0",

    // Koin
    "koin-android" to "io.insert-koin:koin-android:\${versions.koin}",
    "koin-androidx-compose" to "io.insert-koin:koin-androidx-compose:\${versions.koin}",
    "koin-androidx-viewmodel" to "io.insert-koin:koin-androidx-viewmodel:\${versions.koin}",

    // Coroutines
    "coroutines-android" to "org.jetbrains.kotlinx:kotlinx-coroutines-android:\${versions.coroutines}",
    "coroutines-play-services" to "org.jetbrains.kotlinx:kotlinx-coroutines-play-services:\${versions.coroutines}",
    "coroutines-test" to "org.jetbrains.kotlinx:kotlinx-coroutines-test:\${versions.coroutines}",

    // Serialization
    "serialization-json" to "org.jetbrains.kotlinx:kotlinx-serialization-json:\${versions.serialization}",
    "serialization-protobuf" to "org.jetbrains.kotlinx:kotlinx-serialization-protobuf:\${versions.serialization}",

    // Network
    "retrofit" to "com.squareup.retrofit2:retrofit:\${versions.retrofit}",
    "retrofit-moshi" to "com.squareup.retrofit2:converter-moshi:\${versions.retrofit}",
    "retrofit-rxjava3" to "com.squareup.retrofit2:adapter-rxjava3:\${versions.retrofit}",
    "okhttp" to "com.squareup.okhttp3:okhttp:\${versions.okhttp}",
    "okhttp-logging" to "com.squareup.okhttp3:logging-interceptor:\${versions.logging_interceptor_version}",
    "okhttp-tls" to "com.squareup.okhttp3:okhttp-tls:\${versions.okhttp}",
    "moshi-kotlin" to "com.squareup.moshi:moshi-kotlin:\${versions.moshi}",
    "moshi-adapters" to "com.squareup.moshi:moshi-adapters:\${versions.moshi}",
    "moshi-codegen" to "com.squareup.moshi:moshi-kotlin-codegen:\${versions.moshi}",

    // Room
    "room-runtime" to "androidx.room:room-runtime:\${versions.room}",
    "room-ktx" to "androidx.room:room-ktx:\${versions.room}",
    "room-paging" to "androidx.room:room-paging:\${versions.room}",
    "room-compiler" to "androidx.room:room-compiler:\${versions.room}",

    // DataStore
    "datastore-preferences" to "androidx.datastore:datastore-preferences:\${versions.datastore}",
    "datastore-preferences-core" to "androidx.datastore:datastore-preferences-core:\${versions.datastore}",
    "datastore-preferences-rxjava3" to "androidx.datastore:datastore-preferences-rxjava3:\${versions.datastore}",
    "datastore-core" to "androidx.datastore:datastore-core:\${versions.datastore}",

    // Security
    "security-crypto" to "androidx.security:security-crypto:\${versions.security}",
    "security-identity-credential" to "androidx.security:security-identity-credential:\${versions.security}",

    // WorkManager
    "work-runtime-ktx" to "androidx.work:work-runtime-ktx:\${versions.work}",
    "work-rxjava3" to "androidx.work:work-rxjava3:\${versions.work}",
    "work-gcm" to "androidx.work:work-gcm:\${versions.work}",
    "work-testing" to "androidx.work:work-testing:\${versions.work}",

    // Billing
    "billing" to "com.android.billingclient:billing:\${versions.billing}",
    "billing-ktx" to "com.android.billingclient:billing-ktx:\${versions.billing}",

    // Health Connect
    "health-connect" to "androidx.health.connect:connect-client:\${versions.healthConnect}",

    // Firebase
    "firebase-bom" to "com.google.firebase:firebase-bom:\${versions.firebaseBom}",
    "firebase-analytics-ktx" to "com.google.firebase:firebase-analytics-ktx",
    "firebase-crashlytics-ktx" to "com.google.firebase:firebase-crashlytics-ktx",
    "firebase-messaging-ktx" to "com.google.firebase:firebase-messaging-ktx",
    "firebase-auth-ktx" to "com.google.firebase:firebase-auth-ktx",
    "firebase-firestore-ktx" to "com.google.firebase:firebase-firestore-ktx",
    "firebase-config-ktx" to "com.google.firebase:firebase-config-ktx",
    "firebase-perf-ktx" to "com.google.firebase:firebase-perf-ktx",
    "firebase-installations-ktx" to "com.google.firebase:firebase-installations-ktx",

    // Google Play Services
    "play-services-auth" to "com.google.android.gms:play-services-auth:21.0.0",
    "play-services-location" to "com.google.android.gms:play-services-location:21.2.0",
    "play-services-ads-identifier" to "com.google.android.gms:play-services-ads-identifier:18.0.1",

    // Biometric
    "biometric-ktx" to "androidx.biometric:biometric-ktx:\${versions.biometric}",
    "credentials" to "androidx.credentials:credentials:1.3.0-alpha02",
    "credentials-play-services" to "androidx.credentials:credentials-play-services-auth:1.3.0-alpha02",

    // Sentry
    "sentry-android" to "io.sentry:sentry-android:\${versions.sentry}",
    "sentry-ndk" to "io.sentry:sentry-android-ndk:\${versions.sentry}",

    // Coil
    "coil-compose" to "io.coil-kt:coil-compose:\${versions.coil}",
    "coil-network-okhttp" to "io.coil-kt:coil-network-okhttp:\${versions.coil}",

    // Accompanist
    "accompanist-permissions" to "com.google.accompanist:accompanist-permissions:\${versions.accompanist}",
    "accompanist-systemuicontroller" to "com.google.accompanist:accompanist-systemuicontroller:\${versions.accompanist}",
    "accompanist-inset" to "com.google.accompanist:accompanist-inset:\${versions.accompanist}",

    // Material Icons
    "material-icons-extended" to "androidx.compose.material:material-icons-extended",

    // PDF/Export
    "itext-core" to "com.itextpdf:itext7-core:\${versions.itext}",
    "itext-layout" to "com.itextpdf:layout:\${versions.itext}",
    "itext-kernel" to "com.itextpdf:kernel:\${versions.itext}",
    "itext-io" to "com.itextpdf:io:\${versions.itext}",
    "itext-font-asian" to "com.itextpdf:font-asian:\${versions.itext}",
    "opencsv" to "com.opencsv:opencsv:\${versions.opencsv}",

    // Testing
    "junit" to "junit:junit:\${versions.junit}",
    "junit-jupiter" to "org.junit.jupiter:junit-jupiter:\${versions.junitJupiter}",
    "mockito-core" to "org.mockito:mockito-core:\${versions.mockito}",
    "mockito-kotlin" to "org.mockito:mockito-kotlin:\${versions.mockito}",
    "mockk" to "io.mockk:mockk:\${versions.mockk}",
    "robolectric" to "org.robolectric:robolectric:\${versions.robolectric}",
    "core-testing" to "androidx.arch.core:core-testing:2.2.0",
    "lifecycle-testing" to "androidx.lifecycle:lifecycle-runtime-testing:\${versions.lifecycle}",
    "truth" to "com.google.truth:truth:\${versions.truth}",
    "espresso-core" to "androidx.test.espresso:espresso-core:\${versions.espresso}",
    "espresso-contrib" to "androidx.test.espresso:espresso-contrib:\${versions.espresso}",
    "espresso-intents" to "androidx.test.espresso:espresso-intents:\${versions.espresso}",
    "test-rules" to "androidx.test:rules:1.6.0",
    "test-runner" to "androidx.test:runner:1.6.0",
    "compose-ui-test-junit4" to "androidx.compose.ui:ui-test-junit4",
    "compose-ui-test-manifest" to "androidx.compose.ui:ui-test-manifest",
    "compose-ui-tooling" to "androidx.compose.ui:ui-tooling",
    "compose-ui-tooling-preview" to "androidx.compose.ui:ui-tooling-preview"
)