// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    id("com.android.application") version "8.1.1" apply false
    id("org.jetbrains.kotlin.android") version "2.0.0" apply false
    id("com.chaquo.python") version "15.0.1" apply false
    id("com.google.devtools.ksp") version "2.0.0-1.0.23" apply false
    id("com.google.gms.google-services") version "4.4.0" apply false
}

buildscript {
    extra.apply {
        set("koin_version","4.0.0")
        set("crypto_version", "1.1.0-alpha06")
        set("work_version", "2.9.1")
    }
}