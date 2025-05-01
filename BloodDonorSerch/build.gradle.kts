

// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    alias(libs.plugins.android.application) apply false
    id("com.google.gms.google-services") version "4.4.2" apply false
}

buildscript {
    repositories {
        google()  // Ensure Google repository is present
        mavenCentral()
    }
    dependencies {
        classpath("com.google.gms:google-services:4.4.2")  // Keep only one google-services dependency
    }
}

allprojects {
    repositories {
        google()
        mavenCentral()
    }
}

