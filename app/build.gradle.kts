plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("org.jetbrains.kotlin.kapt")
    id("org.jetbrains.kotlin.plugin.serialization")
    id("org.jetbrains.kotlin.plugin.parcelize")
    id("com.google.dagger.hilt.android")
    id("com.google.gms.google-services")
    id("realm-android")
    id("io.gitlab.arturbosch.detekt")
    id("org.jlleitschuh.gradle.ktlint")
}

// Verifica se o keystore de release existe (para evitar falha em builds locais)
val releaseKeystoreExists: Boolean = run {
    val path = System.getenv("KEYSTORE_PATH") ?: "../keystore/release.keystore"
    file(path).exists()
}

android {
    namespace = "com.seuapp.cycletrack"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.lamax.cycletrack"
        minSdk = 26
        targetSdk = 35
        versionCode = 1
        versionName = "1.0.0"
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        multiDexEnabled = true
        vectorDrawables {
            useSupportLibrary = true
        }
    }

    signingConfigs {
        create("release") {
            val keystorePath = System.getenv("KEYSTORE_PATH") ?: "../keystore/release.keystore"
            storeFile = file(keystorePath)
            storePassword = System.getenv("KEYSTORE_PASSWORD")
            keyAlias = System.getenv("KEY_ALIAS")
            keyPassword = System.getenv("KEY_PASSWORD")
        }
    }

    buildTypes {
        release {
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
            // Assign signing config only if keystore is available (release build)
            if (releaseKeystoreExists) {
                signingConfig = signingConfigs.getByName("release")
            }
        }
        debug {
            isMinifyEnabled = false
            isShrinkResources = false
            applicationIdSuffix = ".debug"
            versionNameSuffix = "-debug"
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    kotlinOptions {
        jvmTarget = "17"
        freeCompilerArgs += listOf(
            "-Xopt-in=kotlin.RequiresOptIn",
            "-Xopt-in=kotlinx.coroutines.ExperimentalCoroutinesApi",
            "-Xopt-in=kotlinx.serialization.ExperimentalSerializationApi",
            "-Xopt-in=androidx.lifecycle.ExperimentalLifecycleApi",
            "-Xopt-in=androidx.compose.material3.ExperimentalMaterial3Api",
            "-Xopt-in=androidx.health.connect.client.ExperimentalHealthConnectApi"
        )
    }

    composeOptions {
        // 1.5.8 = par oficial com Kotlin 1.9.22 (1.5.11 exige Kotlin 1.9.23)
        kotlinCompilerExtensionVersion = "1.5.8"
    }

    packagingOptions {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1,LICENSE,LICENSE.txt,NOTICE,NOTICE.txt}"
        }
        jniLibs {
            useLegacyPackaging = true
        }
    }

    buildFeatures {
        compose = true
        dataBinding = true
        viewBinding = true
    }
}

dependencies {
    // ============ Core Android ============
    val core_ktx_version = "1.13.1"
    val appcompat_version = "1.7.0"
    val activity_version = "1.9.0"
    val fragment_version = "1.8.0"
    val lifecycle_version = "2.8.0"
    val savedstate_version = "1.2.1"

    implementation("androidx.core:core-ktx:$core_ktx_version")
    implementation("androidx.multidex:multidex:2.0.1")
    implementation("androidx.appcompat:appcompat:$appcompat_version")
    implementation("androidx.activity:activity-compose:$activity_version")
    implementation("androidx.activity:activity-ktx:$activity_version")
    implementation("androidx.fragment:fragment-ktx:$fragment_version")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:$lifecycle_version")
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:$lifecycle_version")
    implementation("androidx.lifecycle:lifecycle-livedata-ktx:$lifecycle_version")
    implementation("androidx.lifecycle:lifecycle-runtime-compose:$lifecycle_version")
    implementation("androidx.lifecycle:lifecycle-process:$lifecycle_version")
    implementation("androidx.savedstate:savedstate-ktx:$savedstate_version")

    // ============ Compose BOM ============
    val compose_bom_version = "2024.04.01"
    implementation(platform("androidx.compose:compose-bom:$compose_bom_version"))
    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.ui:ui-graphics")
    implementation("androidx.compose.ui:ui-tooling-preview")
    implementation("androidx.compose.material3:material3")
    implementation("androidx.compose.material3:material3-window-size-class")
    implementation("androidx.compose.runtime:runtime-livedata")
    implementation("androidx.compose.runtime:runtime-rxjava3")
    implementation("androidx.compose.animation:animation")
    implementation("androidx.compose.animation:animation-graphics")
    implementation("androidx.compose.foundation:foundation")
    implementation("androidx.compose.foundation:foundation-layout")

    // ============ Navigation ============
    val navigation_version = "2.8.0"
    implementation("androidx.navigation:navigation-compose:$navigation_version")
    implementation("androidx.navigation:navigation-fragment-ktx:$navigation_version")
    implementation("androidx.navigation:navigation-ui-ktx:$navigation_version")
    implementation("androidx.navigation:navigation-dynamic-features-fragment:$navigation_version")

    // ============ Hilt (DI) ============
    val hilt_version = "2.48"
    implementation("com.google.dagger:hilt-android:$hilt_version")
    // hilt-navigation-compose vive no grupo androidx.hilt (Google Maven)
    implementation("androidx.hilt:hilt-navigation-compose:1.2.0")
    kapt("com.google.dagger:hilt-compiler:$hilt_version")
    kapt("androidx.hilt:hilt-compiler:1.2.0")

    // ============ Koin (Alternative DI for Domain) ============
    val koin_version = "3.5.6"
    implementation("io.insert-koin:koin-android:$koin_version")
    implementation("io.insert-koin:koin-androidx-compose:$koin_version")

    // ============ Coroutines & Flow ============
    val coroutines_version = "1.8.0"
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:$coroutines_version")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-play-services:$coroutines_version")

    // ============ Serialization ============
    val serialization_version = "1.6.3"
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:$serialization_version")

    // ============ DateTime ============
    implementation("org.jetbrains.kotlinx:kotlinx-datetime:0.5.0")

    // ============ Network ============
    val retrofit_version = "2.11.0"
    val okhttp_version = "4.12.0"
    val logging_interceptor_version = "4.12.0"

    implementation("com.squareup.retrofit2:retrofit:$retrofit_version")
    implementation("com.squareup.retrofit2:converter-moshi:$retrofit_version")
    implementation("com.squareup.retrofit2:adapter-rxjava3:$retrofit_version")
    implementation("com.squareup.okhttp3:okhttp:$okhttp_version")
    implementation("com.squareup.okhttp3:logging-interceptor:$logging_interceptor_version")
    implementation("com.squareup.okhttp3:okhttp-urlconnection:$okhttp_version")
    // Certificate pinning
    implementation("com.squareup.okhttp3:okhttp-tls:$okhttp_version")

    // Moshi
    val moshi_version = "1.15.1"
    implementation("com.squareup.moshi:moshi-kotlin:$moshi_version")
    implementation("com.squareup.moshi:moshi-adapters:$moshi_version")
    kapt("com.squareup.moshi:moshi-kotlin-codegen:$moshi_version")

    // ============ Realm (Primary DB) ============
    // API Java do Realm (io.realm.RealmObject / RealmList / @PrimaryKey)
    // Plugin 'realm-android' aplicado no bloco plugins acima
    implementation("io.realm:realm-android-library:10.19.0")

    // ============ Timber (Logging) ============
    implementation("com.jakewharton.timber:timber:5.0.1")

    // ============ RxJava3 (DataStore Rx + Retrofit adapter) ============
    implementation("io.reactivex.rxjava3:rxjava:3.1.8")
    implementation("io.reactivex.rxjava3:rxandroid:3.0.2")

    // ============ Room (Analytics/Events) ============
    val room_version = "2.6.1"
    implementation("androidx.room:room-runtime:$room_version")
    implementation("androidx.room:room-ktx:$room_version")
    implementation("androidx.room:room-paging:$room_version")
    kapt("androidx.room:room-compiler:$room_version")

    // ============ DataStore (Preferences) ============
    val datastore_version = "1.1.1"
    implementation("androidx.datastore:datastore-preferences:$datastore_version")
    implementation("androidx.datastore:datastore-preferences-core:$datastore_version")
    implementation("androidx.datastore:datastore-preferences-rxjava3:$datastore_version")
    implementation("androidx.datastore:datastore-core:$datastore_version")

    // ============ EncryptedSharedPreferences (Tokens/Keys) ============
    val security_version = "1.1.0-alpha06"
    implementation("androidx.security:security-crypto:$security_version")

    // ============ WorkManager (Offline Sync) ============
    val work_version = "2.9.0"
    implementation("androidx.work:work-runtime-ktx:$work_version")
    implementation("androidx.work:work-rxjava3:$work_version")
    implementation("androidx.work:work-gcm:$work_version")
    androidTestImplementation("androidx.work:work-testing:$work_version")

    // ============ Firebase ============
    // Apenas Auth: o app e local-first (Realm) e nao usa Analytics/Messaging/
    // Firestore/Remote Config nesta versao, para nao coletar telemetria que
    // contradiga a promessa de privacidade feita no onboarding.
    val firebase_bom_version = "33.1.0"
    implementation(platform("com.google.firebase:firebase-bom:$firebase_bom_version"))
    implementation("com.google.firebase:firebase-auth-ktx")

    // ============ Biometric ============
    val biometric_version = "1.2.0-alpha04"
    implementation("androidx.biometric:biometric-ktx:$biometric_version")

    // ============ Sentry (Error Tracking) - DISABLED in DEBUG ============
    // val sentry_version = "7.11.0"
    // implementation("io.sentry:sentry-android:$sentry_version")
    // implementation("io.sentry:sentry-android-ndk:$sentry_version")

    // ============ Coil (Image Loading) ============
    val coil_version = "2.6.0"
    implementation("io.coil-kt:coil-compose:$coil_version")

    // ============ Accompanist (Permissions, System UI) ============
    // Removido: nao utilizado no codigo atual (evita downloads extras)

    // ============ Material Icons Extended ============
    implementation("androidx.compose.material:material-icons-extended")

    // ============ Material Components (temas XML Material3) ============
    // Necessario para Theme.Material3.* usado em themes.xml
    implementation("com.google.android.material:material:1.12.0")

    // ============ PDF Generation (Export) ============
    implementation("com.itextpdf:itext7-core:7.2.5")
    implementation("com.itextpdf:layout:7.2.5")
    implementation("com.itextpdf:kernel:7.2.5")
    implementation("com.itextpdf:io:7.2.5")
    implementation("com.itextpdf:font-asian:7.2.5")

    // ============ CSV Export ============
    implementation("com.opencsv:opencsv:5.9")

    // ============ Testing ============
    testImplementation("junit:junit:4.13.2")
    testImplementation("org.junit.jupiter:junit-jupiter:5.10.2")
    testImplementation("org.mockito:mockito-core:5.11.0")
    testImplementation("org.mockito:mockito-kotlin:5.11.0")
    testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:$coroutines_version")
    testImplementation("androidx.arch.core:core-testing:2.2.0")
    testImplementation("androidx.lifecycle:lifecycle-runtime-testing:$lifecycle_version")
    testImplementation("com.google.truth:truth:1.1.5")
    testImplementation("com.nhaarman.mockitokotlin2:mockito-kotlin:2.2.0")
    testImplementation("io.mockk:mockk:1.13.12")
    testImplementation("org.robolectric:robolectric:4.11")

    androidTestImplementation("androidx.test.ext:junit:1.2.1")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
    androidTestImplementation("androidx.test.espresso:espresso-contrib:3.5.1")
    androidTestImplementation("androidx.test.espresso:espresso-intents:3.5.1")
    androidTestImplementation("androidx.test:rules:1.6.0")
    androidTestImplementation("androidx.test:runner:1.6.0")
    androidTestImplementation("androidx.compose.ui:ui-test-junit4")
    androidTestImplementation("androidx.compose.ui:ui-test-manifest")
    debugImplementation("androidx.compose.ui:ui-tooling")
    debugImplementation("androidx.compose.ui:ui-tooling-preview")

    // ============ Detekt / Lint ============
    // Applied via gradle plugins in root build.gradle.kts

    // ============ KSP (for Room, Moshi, Protobuf) ============
    // Applied via plugins in root build.gradle.kts
}

kapt {
    correctErrorTypes = true
    javacOptions {
        option("-Xlint:unchecked")
    }
}

// Tarefa temporaria de diagnostico: grava o classpath de compilacao em arquivo
tasks.register("dumpCompileClasspath") {
    doLast {
        val sdkDir = android.sdkDirectory
        val androidJar = File(sdkDir, "platforms/android-35/android.jar")
        val rJar = file("build/intermediates/compile_and_runtime_not_namespaced_r_class_jar/debug/processDebugResources/R.jar")
        val kotlinTask = tasks.getByName("compileDebugKotlin") as org.jetbrains.kotlin.gradle.tasks.KotlinCompile
        val deps = kotlinTask.libraries.files.map { it.absolutePath }
        val out = (listOf(androidJar.absolutePath, rJar.absolutePath) + deps).joinToString(";")
        file("build/compile-classpath.txt").writeText(out)
        println("CLASSPATH_DUMP_OK")
    }
}

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
    kotlinOptions {
        freeCompilerArgs += listOf(
            "-Xopt-in=kotlin.RequiresOptIn",
            "-Xopt-in=kotlinx.coroutines.ExperimentalCoroutinesApi",
            "-Xopt-in=kotlinx.serialization.ExperimentalSerializationApi",
            "-Xopt-in=androidx.lifecycle.ExperimentalLifecycleApi",
            "-Xopt-in=androidx.compose.material3.ExperimentalMaterial3Api",
            "-Xopt-in=androidx.health.connect.client.ExperimentalHealthConnectApi",
            "-Xjsr305=strict"
        )
    }
}

// ============ Detekt ============
detekt {
    buildUponDefaultConfig = true
    allRules = false
    config.setFrom(rootProject.file("detekt.yml"))
    baseline = file("$projectDir/detekt-baseline.xml")
    parallel = true
}

tasks.withType<io.gitlab.arturbosch.detekt.Detekt>().configureEach {
    jvmTarget = "17"
    reports {
        html.required.set(true)
        xml.required.set(true)
        sarif.required.set(true)
        txt.required.set(false)
        md.required.set(false)
    }
}

// ============ Ktlint ============
ktlint {
    version = "0.50.0"
    android.set(true)
    ignoreFailures.set(true)
    filter {
        exclude { it.file.path.contains("${'$'}{projectDir}/build/") }
        exclude { it.file.path.contains("/generated/") }
    }
}