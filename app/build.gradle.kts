plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("com.chaquo.python")
    id("com.google.devtools.ksp")
    id("com.google.gms.google-services")
}

android {
    namespace = "it.antonino.palco"
    compileSdk = 34

    defaultConfig {
        applicationId = "it.antonino.palco"
        minSdk = 24
        targetSdk = 34
        versionCode = 40
        versionName = "2.0"

        ndk {
            // On Apple silicon, you can omit x86_64.
            abiFilters += listOf("armeabi-v7a", "arm64-v8a", "x86_64","x86")
        }

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
            ndk {
                debugSymbolLevel = "FULL"
            }
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
    buildFeatures {
        viewBinding = true
    }
    buildToolsVersion = "30.0.3"
}

chaquopy {
    defaultConfig {
        pip {
            install("bs4")
            install("numpy")
        }
    }
}

dependencies {
    implementation("androidx.core:core-ktx:1.13.1")
    implementation("androidx.appcompat:appcompat:1.7.0")
    implementation("com.google.android.material:material:1.12.0")
    implementation("io.insert-koin:koin-android:${rootProject.extra["koin_version"]}")
    implementation("io.insert-koin:koin-core-viewmodel:${rootProject.extra["koin_version"]}")
    implementation("androidx.security:security-crypto-ktx:${rootProject.extra["crypto_version"]}")
    implementation("androidx.room:room-runtime:${rootProject.extra["room_version"]}")
    annotationProcessor("androidx.room:room-compiler:${rootProject.extra["room_version"]}")
    implementation("androidx.room:room-ktx:${rootProject.extra["room_version"]}")
    ksp("androidx.room:room-compiler:${rootProject.extra["room_version"]}")
    implementation("androidx.work:work-runtime:${rootProject.extra["work_version"]}")
    implementation("androidx.work:work-runtime-ktx:${rootProject.extra["work_version"]}")
    implementation(platform("com.google.firebase:firebase-bom:32.6.0"))
    implementation("org.apache.commons:commons-lang3:3.16.0")
    implementation("com.github.sundeepk:compact-calendar-view:3.0.0")
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")
    implementation("com.google.code.gson:gson:2.11.0")
    implementation("com.google.firebase:firebase-analytics")
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.2.1")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.6.1")
}