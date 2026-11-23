# ============ ProGuard/R8 Rules for CycleTrack ============

# ---- Keep Application Class ----
-keep public class com.seuapp.cycletrack.CycleTrackApplication {
    public protected *;
}

# ---- Hilt / Dagger ----
-keep class dagger.hilt.** { *; }
-keep class com.seuapp.cycletrack.di.** { *; }
-keepclasseswithmembers class * {
    @dagger.hilt.android.* <init>(...);
}
-keep @interface dagger.hilt.android.*

# ---- Kotlin / Coroutines / Serialization ----
-keep class kotlin.** { *; }
-keep class kotlinx.coroutines.** { *; }
-keep class kotlinx.serialization.** { *; }
-keepclassmembers class * {
    @kotlinx.serialization.Serializable *;
}
-keepclassmembers class * implements kotlinx.serialization.KSerializer {
    <fields>;
}

# ---- Realm ----
-keep class io.realm.** { *; }
-keep class com.seuapp.cycletrack.data.local.realm.** { *; }
-keepclassmembers class * extends io.realm.RealmObject {
    *;
}
-keepclassmembers class * implements io.realm.RealmModel {
    *;
}
-keep @interface io.realm.annotations.**

# ---- Room ----
-keep class androidx.room.** { *; }
-keep class com.seuapp.cycletrack.data.local.room.** { *; }
-keep @interface androidx.room.**
-dontwarn androidx.room.paging.**

# ---- DataStore / Protobuf ----
-keep class androidx.datastore.** { *; }
-keep class com.google.protobuf.** { *; }
-keep class com.seuapp.cycletrack.data.local.datastore.** { *; }

# ---- Retrofit / OkHttp / Moshi ----
-keep class retrofit2.** { *; }
-keep class okhttp3.** { *; }
-keep class okio.** { *; }
-keep class com.squareup.moshi.** { *; }
-keep class com.seuapp.cycletrack.data.remote.** { *; }
-keep interface com.seuapp.cycletrack.data.remote.api.** { *; }
-keepclassmembers class * {
    @com.squareup.moshi.* *;
}
-keep @interface com.squareup.moshi.**

# ---- Moshi Adapters ----
-keep class * implements com.squareup.moshi.JsonAdapter {
    <init>(...);
}

# ---- Navigation ----
-keep class androidx.navigation.** { *; }
-keepnames class * implements androidx.navigation.Navigator

# ---- WorkManager ----
-keep class androidx.work.** { *; }
-keep class com.seuapp.cycletrack.work.** { *; }

# ---- Play Billing ----
-keep class com.android.billingclient.** { *; }
-keep class com.seuapp.cycletrack.billing.** { *; }

# ---- Health Connect ----
-keep class androidx.health.connect.** { *; }
-keep class com.seuapp.cycletrack.healthconnect.** { *; }

# ---- Firebase ----
-keep class com.google.firebase.** { *; }
-dontwarn com.google.firebase.**

# ---- Google Play Services ----
-keep class com.google.android.gms.** { *; }

# ---- Biometric / Credentials ----
-keep class androidx.biometric.** { *; }
-keep class androidx.credentials.** { *; }

# ---- Sentry ----
-keep class io.sentry.** { *; }
-dontwarn io.sentry.**

# ---- Coil ----
-keep class coil3.** { *; }
-keep class com.seuapp.cycletrack.ui.** { *; }

# ---- Accompanist ----
-keep class com.google.accompanist.** { *; }

# ---- iText (PDF) ----
-keep class com.itextpdf.** { *; }
-dontwarn com.itextpdf.**

# ---- OpenCSV ----
-keep class com.opencsv.** { *; }

# ---- Security Crypto ----
-keep class androidx.security.crypto.** { *; }

# ---- App Specific: Domain Models ----
-keep class com.seuapp.cycletrack.domain.model.** { *; }
-keep class com.seuapp.cycletrack.domain.usecase.** { *; }
-keep class com.seuapp.cycletrack.domain.repository.** { *; }
-keep class com.seuapp.cycletrack.presentation.** { *; }

# ---- Parcelable ----
-keep class * implements android.os.Parcelable {
    public static final android.os.Parcelable$Creator *;
}

# ---- Enum ----
-keepclassmembers enum * {
    public static **[] values();
    public static ** valueOf(java.lang.String);
}

# ---- Annotation Processors ----
-keep @interface *
-keep @interface *.*
-keepclassmembers class * {
    @* *;
}

# ---- Optimization ----
-optimizations !code/simplification/arithmetic,!field/*,!class/merging/*
-optimizationpasses 5
-allowaccessmodification

# ---- Keep Line Numbers for Crash Reports ----
-keepattributes SourceFile,LineNumberTable

# ---- Remove Logging in Release ----
-assumenosideeffects class android.util.Log {
    public static int v(...);
    public static int d(...);
    public static int i(...);
    public static int w(...);
}
-assumenosideeffects class timber.log.Timber {
    public static *;
}

# ---- Keep Names for Reflection/Serialization ----
-keepnames class com.seuapp.cycletrack.** { *; }
-keepclassmembernames class com.seuapp.cycletrack.** { *; }

# ---- Exclude Specific Warnings ----
-dontwarn org.codehaus.mojo.animal_sniffer.IgnoreJRERequirement
-dontwarn kotlin.reflect.jvm.internal.**
-dontwarn kotlinx.coroutines.debug.**
-dontwarn androidx.compose.runtime.**
-dontwarn org.jetbrains.annotations.**