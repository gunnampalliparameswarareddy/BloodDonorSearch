plugins {
    alias(libs.plugins.android.application)
    id("com.google.gms.google-services")
}

android {
    namespace = "com.main.blooddonorserch"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.main.blooddonorserch"
        minSdk = 24
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }

    buildFeatures {
        dataBinding = true
        viewBinding = true
    }
}

dependencies {
    implementation(libs.navigation.fragment)
    implementation(libs.navigation.ui)
    implementation(libs.firebase.storage)

    val room_version = "2.6.1"
    val lifecycle_version = "2.8.7"

    // Firebase BoM (always use the latest)
    implementation(platform("com.google.firebase:firebase-bom:32.7.1"))
    implementation("com.google.firebase:firebase-auth")
    implementation("com.google.firebase:firebase-firestore")
    implementation("com.google.firebase:firebase-database")
    implementation("com.google.firebase:firebase-storage")
//    implementation("com.google.firebase:firebase-analytics")

    //implementation("com.google.firebase:firebase-auth:22.3.1") // Latest Firebase Auth
    //implementation("com.google.firebase:firebase-firestore:24.10.1") // Firestore (if used)
    //implementation("com.google.firebase:firebase-database:20.3.0") // Realtime Database (if used)
    //implementation("com.google.firebase:firebase-analytics:21.6.1") // Firebase Analytics (optional)

    // Google Play Services Auth
    implementation("com.google.android.gms:play-services-auth:20.7.0")

    // AndroidX Lifecycle
    implementation("androidx.lifecycle:lifecycle-viewmodel:$lifecycle_version")
    implementation("androidx.lifecycle:lifecycle-livedata:$lifecycle_version")

    // Room Database
    implementation("androidx.room:room-runtime:$room_version")
    annotationProcessor("androidx.room:room-compiler:$room_version")

    // UI Components
    implementation("com.google.android.material:material:1.9.0")

    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)
    implementation(libs.annotation)
    implementation(libs.lifecycle.livedata.ktx)
    implementation(libs.lifecycle.viewmodel.ktx)
    implementation(libs.databinding.runtime)

    // Testing
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
}

// Force Dependency Resolution (if conflict still occurs)
configurations.all {
    resolutionStrategy {
        force("com.google.firebase:firebase-common:20.3.1")
        force("com.google.firebase:firebase-firestore:24.10.1")
    }
}
