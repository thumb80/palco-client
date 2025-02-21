// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    id("com.android.application") version "8.8.0" apply false
    id("org.jetbrains.kotlin.android") version "2.1.10" apply false
    id("com.chaquo.python") version "15.0.1" apply false
    id("com.google.devtools.ksp") version "2.1.10-1.0.30" apply false
    id("com.google.gms.google-services") version "4.4.0" apply false
}

buildscript {
    extra.apply {
        set("koin_version","4.0.0")
        set("crypto_version", "1.1.0-alpha06")
        set("work_version", "2.10.0")
        set("junit_version", "4.13.2")
        set("androidx_test_version","1.6.1")
        set("mockito_version","5.12.0")
        set("mockito_kotlin_version","4.0.0")
        set("mockkVersion","1.4.1")
    }
}