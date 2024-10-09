# Add project specific ProGuard rules here.
# You can control the set of applied configuration files using the
# proguardFiles setting in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}

# Uncomment this to preserve the line number information for
# debugging stack traces.
#-keepattributes SourceFile,LineNumberTable

# If you keep the line number information, uncomment this to
# hide the original source file name.
#-renamesourcefileattribute SourceFile


# WorkManager-related rules
-keep class androidx.work.** { *; }
-dontwarn androidx.work.**

# For Kotlin Coroutines
-keepclassmembers class kotlinx.coroutines.** { *; }
-dontwarn kotlinx.coroutines.**

# Prevent the removal of CoroutineWorker
-keep class androidx.work.ListenableWorker { *; }
-keep class androidx.work.CoroutineWorker { *; }

# Keep WorkManager worker annotations
-keep @interface androidx.work.Worker
-keepclassmembers class * {
    @androidx.work.Worker *;
}

# Prevent stripping of classes used for WorkManager initialization
-keep class androidx.startup.Initializer { *; }
-keep class androidx.work.WorkerParameters { *; }

# Preserve Chauopy SDK classes and methods
-keep class com.chauopy.** { *; }
-dontwarn com.chauopy.**

# Preserve any native methods
-keepclassmembers class * {
    native <methods>;
}

# Preserve reflection-based accesses (if used)
-keepclassmembers class ** {
    *;
}

# Keep all classes in the Apache Commons Lang3 package
-keep class org.apache.commons.lang3.** { *; }
-dontwarn org.apache.commons.lang3.**

# Keep Retrofit interfaces (needed for dynamic proxy generation)
-keep interface retrofit2.** { *; }

# Keep Retrofit classes (core components)
-keep class retrofit2.** { *; }
-dontwarn retrofit2.**

# Keep the method annotations used by Retrofit for HTTP calls (GET, POST, etc.)
-keepattributes Signature
-keepattributes *Annotation*

# If you're using OkHttp (which is common with Retrofit)
-keep class okhttp3.** { *; }
-dontwarn okhttp3.**

# If you're using converters, like Gson, Moshi, or Scalars

## Gson (if using GsonConverterFactory with Retrofit)
-keep class com.google.gson.** { *; }
-dontwarn com.google.gson.**

