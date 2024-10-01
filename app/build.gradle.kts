plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.jetbrains.kotlin.android)
}

android {
    namespace = "com.example.weather"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.weather"
        minSdk = 24
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
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    implementation (libs.retrofit.v290)
    implementation (libs.converter.gson.v290)
    implementation (libs.play.services.location)

    implementation(files("libs/OlaMapSdk-1.5.0.aar")) // Path to the AAR file or Use Absolute Path

    //Maplibre
    implementation (libs.android.sdk.v1002)

    // Required for Ola-MapsSdk
    implementation (libs.moe.android.sdk)
    implementation (libs.android.sdk)
    implementation (libs.android.sdk.directions.models)
    implementation (libs.android.sdk.services)
    implementation (libs.android.sdk.turf)
    implementation (libs.android.plugin.markerview.v9)
    implementation (libs.android.plugin.annotation.v9)


    implementation (libs.androidx.lifecycle.extensions)
    implementation (libs.androidx.lifecycle.compiler)

    implementation (libs.glide)
}