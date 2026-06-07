plugins {
    alias(libs.plugins.android.application)
}

android {
    namespace = "com.example.fitnesstracker"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.fitnesstracker"
        minSdk = 26
        targetSdk = 34
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
        viewBinding = true
    }
}

dependencies {
    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)
    
    // Room
    implementation(libs.room.runtime)
    annotationProcessor(libs.room.compiler)
    implementation(libs.room.guava)
    
    // Lifecycle
    implementation(libs.lifecycle.service)
    implementation(libs.lifecycle.viewmodel)
    
    // WearOS
    implementation(libs.play.services.wearable)
    
    // Gemini API & JSON
    implementation(libs.generativeai)
    implementation(libs.gson)
    implementation("com.google.guava:guava:33.0.0-android")

    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
}
