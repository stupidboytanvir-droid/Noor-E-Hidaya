# ===================================================================
# Noor-e-Hidayah (নুর-এ-হেদায়েত) Production ProGuard / R8 Rules
# Enterprise Code Shrinking, Resource Stripping & Decompile Protection
# ===================================================================

# 1. Line numbers and debugging attributes for stack trace symbolication
-keepattributes SourceFile,LineNumberTable
-keepattributes *Annotation*
-keepattributes Signature
-keepattributes InnerClasses,EnclosingMethod

# 2. Android Architecture & Jetpack Compose
-dontwarn androidx.compose.**
-keep class androidx.compose.** { *; }
-keep class com.example.ui.theme.** { *; }

# 3. Room Database & Local SQLite Persistence
-keep class * extends androidx.room.RoomDatabase
-dontwarn androidx.room.paging.**
-keep @androidx.room.Entity class * { *; }
-keep @androidx.room.Dao interface * { *; }
-keep class com.example.data.local.** { *; }
-keepclassmembers class * extends androidx.room.RoomDatabase {
    <init>();
}

# 4. Data Models, Enums & Islamic Domain Classes
-keep class com.example.data.model.** { *; }
-keep enum com.example.data.model.** { *; }
-keep class com.example.data.mosque.** { *; }
-keep class com.example.data.location.** { *; }
-keep class com.example.data.preference.** { *; }

# 5. Moshi & Retrofit API Serialization
-keep @com.squareup.moshi.JsonClass class * { *; }
-keepclassmembers class * {
    @com.squareup.moshi.Json <fields>;
}
-dontwarn com.squareup.moshi.**
-dontwarn retrofit2.**
-keep class retrofit2.** { *; }

# 6. Firebase (Auth, Firestore, App Check) & Google Play Services
-keep class com.google.firebase.** { *; }
-keepclassmembers class * {
    @com.google.firebase.firestore.PropertyName <fields>;
    @com.google.firebase.firestore.PropertyName <methods>;
    @com.google.firebase.firestore.ServerTimestamp <fields>;
}
-keep class androidx.credentials.** { *; }
-keep class com.google.android.libraries.identity.googleid.** { *; }

# 7. Google Play In-App Updates
-keep class com.google.android.play.core.** { *; }
-dontwarn com.google.android.play.core.**

# 8. Kotlin Coroutines & Asynchronous Dispatchers
-keepnames class kotlinx.coroutines.internal.MainDispatcherFactory {}
-keepnames class kotlinx.coroutines.CoroutineExceptionHandler {}
-dontwarn kotlinx.coroutines.**

# 9. App Security Guard & Runtime Integrity
-keep class com.example.data.security.** { *; }
-keepclassmembers class com.example.data.security.** {
    public *;
}
-keep class com.example.BuildConfig { *; }

# 10. Coil Image Loading
-dontwarn coil.**
-keep class coil.** { *; }

# 11. OSMDroid OpenStreetMap Engine
-keep class org.osmdroid.** { *; }
-dontwarn org.osmdroid.**

# 12. Strip debug and verbose logs in production release builds
-assumenosideeffects class android.util.Log {
    public static boolean isLoggable(java.lang.String, int);
    public static int v(...);
    public static int d(...);
}

