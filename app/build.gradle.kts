import java.io.FileInputStream
import java.util.Properties

plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("com.chaquo.python")
    id("com.google.devtools.ksp")
    id("com.google.gms.google-services")
    id("jacoco")
}

val localProps = Properties()
localProps.load(FileInputStream(file("../local.properties")))
val discogsApiKey = localProps["discogsApiKey"] as String
val discogsSecret = localProps["discogsSecret"] as String

android {
    namespace = "it.antonino.palco"
    compileSdk = 36

    defaultConfig {
        applicationId = "it.antonino.palco"
        minSdk = 24
        targetSdk = 36
        versionCode = 58
        versionName = "4.20"

        buildConfigField("String", "DiscogsApiKey", discogsApiKey)
        buildConfigField("String","DiscogsSecret", discogsSecret)

        ndk {
            // On Apple silicon, you can omit x86_64.
            abiFilters += listOf("arm64-v8a", "x86_64")
        }

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        debug {
            enableUnitTestCoverage = true
            enableAndroidTestCoverage = true
        }
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
        buildConfig = true
    }

    useLibrary("android.test.mock")

    buildToolsVersion = "35.0.0"
    ndkVersion = "25.1.8937393"

}

jacoco {
    toolVersion = "0.8.12"
}

tasks {
    val jacocoTestReport by registering(org.gradle.testing.jacoco.tasks.JacocoReport::class) {
        dependsOn("testDebugUnitTest") // O il task che vuoi eseguire per i test

        reports {
            xml.required = true
            html.required = true
            html.outputLocation = file("build/jacoco/reports/jacoco/testDebugUnitTest/html")
        }

        val fileFilter = arrayOf("**/R.class", "**/R$*.class", "**/BuildConfig.*", "**/Manifest*.*")

        sourceDirectories = files(arrayOf("src/main/java"))


        executionData = files("build/jacoco/testDebugUnitTest.exec")
    }
}

chaquopy {
    defaultConfig {
        pip {
            install("bs4")
            install("numpy")
            install("DateTime")
            install("beautifulsoup4")
        }
    }
}

dependencies {
    implementation("androidx.core:core-ktx:1.17.0")
    implementation("androidx.appcompat:appcompat:1.7.1")
    implementation("com.google.android.material:material:1.13.0")
    implementation("io.insert-koin:koin-android:${rootProject.extra["koin_version"]}")
    implementation("io.insert-koin:koin-core-viewmodel:${rootProject.extra["koin_version"]}")
    implementation("io.insert-koin:koin-androidx-workmanager:${rootProject.extra["koin_version"]}")
    implementation("androidx.security:security-crypto-ktx:${rootProject.extra["crypto_version"]}")
    implementation("androidx.work:work-runtime:${rootProject.extra["work_version"]}")
    implementation("androidx.work:work-runtime-ktx:${rootProject.extra["work_version"]}")
    implementation(platform("com.google.firebase:firebase-bom:34.2.0"))
    implementation("org.apache.commons:commons-text:1.14.0")
    implementation("androidx.viewpager2:viewpager2:1.1.0")
    implementation("com.github.sundeepk:compact-calendar-view:3.0.0")
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")
    implementation("com.google.code.gson:gson:2.11.0")
    implementation("com.google.firebase:firebase-analytics")
    implementation("com.eftimoff:android-pathview:1.0.8@aar")
    implementation("androidx.test.ext:junit-ktx:1.3.0")
    implementation("androidx.work:work-testing:2.10.3")
    implementation("com.github.bumptech.glide:glide:4.16.0")
    implementation("com.jakewharton.threetenabp:threetenabp:1.4.5")
    implementation("androidx.constraintlayout:constraintlayout:2.2.1")
    implementation("org.osmdroid:osmdroid-android:6.1.20")
    testImplementation("junit:junit:${rootProject.extra["junit_version"]}")
    testImplementation("androidx.test:core:${rootProject.extra["androidx_test_version"]}")
    testImplementation("org.mockito:mockito-core:${rootProject.extra["mockito_version"]}")
    testImplementation("org.mockito.kotlin:mockito-kotlin:${rootProject.extra["mockito_kotlin_version"]}")
    testImplementation("io.mockk:mockk:${rootProject.extra["mockkVersion"]}")
    testImplementation("io.insert-koin:koin-test:4.0.2")
    testImplementation("io.insert-koin:koin-test-junit4:4.0.2")
    testImplementation("android.arch.core:core-testing:1.1.1")
    androidTestImplementation("io.insert-koin:koin-test-jvm:4.0.2")
    androidTestImplementation("androidx.test.ext:junit:1.2.1")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.6.1")
    androidTestImplementation("androidx.test:runner:1.6.2")
}

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(17)
    }
}
