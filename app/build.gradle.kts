import com.android.build.gradle.internal.packaging.defaultExcludes
import org.gradle.internal.impldep.bsh.commands.dir

plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("com.chaquo.python")
    id("com.google.devtools.ksp")
    id("com.google.gms.google-services")
    id("jacoco")
}

android {
    namespace = "it.antonino.palco"
    compileSdk = 35

    defaultConfig {
        applicationId = "it.antonino.palco"
        minSdk = 24
        targetSdk = 35
        versionCode = 49
        versionName = "3.3"

        ndk {
            // On Apple silicon, you can omit x86_64.
            abiFilters += listOf("armeabi-v7a", "arm64-v8a", "x86_64","x86")
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
    }

    buildToolsVersion = "35.0.0"

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
            install("pandas")
        }
    }
}

dependencies {
    implementation("androidx.core:core-ktx:1.15.0")
    implementation("androidx.appcompat:appcompat:1.7.0")
    implementation("com.google.android.material:material:1.12.0")
    implementation("io.insert-koin:koin-android:${rootProject.extra["koin_version"]}")
    implementation("io.insert-koin:koin-core-viewmodel:${rootProject.extra["koin_version"]}")
    implementation("io.insert-koin:koin-androidx-workmanager:${rootProject.extra["koin_version"]}")
    implementation("androidx.security:security-crypto-ktx:${rootProject.extra["crypto_version"]}")
    implementation("androidx.work:work-runtime:${rootProject.extra["work_version"]}")
    implementation("androidx.work:work-runtime-ktx:${rootProject.extra["work_version"]}")
    implementation(platform("com.google.firebase:firebase-bom:32.6.0"))
    implementation("org.apache.commons:commons-lang3:3.16.0")
    implementation("com.github.sundeepk:compact-calendar-view:3.0.0")
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")
    implementation("com.google.code.gson:gson:2.11.0")
    implementation("com.google.firebase:firebase-analytics")
    implementation("com.eftimoff:android-pathview:1.0.8@aar")
    implementation("androidx.test.ext:junit-ktx:1.2.1")
    implementation("androidx.work:work-testing:2.10.0")
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
