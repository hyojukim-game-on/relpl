import org.jetbrains.kotlin.ir.backend.js.compile

plugins {
    alias(libs.plugins.androidLibrary)
    alias(libs.plugins.kotlinAndroid)
    alias(libs.plugins.kapt)
    alias(libs.plugins.daggerHilt)
    alias(libs.plugins.googleservice)
}

android {
    namespace = "com.gdd.presentation"
    compileSdk = 33

    defaultConfig {
        minSdk = 26

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        consumerProguardFiles("consumer-rules.pro")
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
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions {
        jvmTarget = "17"
    }
    buildFeatures{
        viewBinding = true
        dataBinding = true
    }
}

dependencies {
    implementation(project(":domain"))
    implementation(project(":retrofit_adapter"))
    implementation(project(":btmsheet"))

    // Android
    implementation(libs.bundles.androidx)
    testImplementation(libs.bundles.testing)

    // Hilt
    implementation(libs.hilt.android)
    kapt(libs.hilt.compiler)

    // Room
    implementation(libs.room.runtime)
    implementation(libs.room.ktx)
    kapt(libs.room.compiler)

    // Retrofit
    implementation(libs.bundles.retrofit)

    // UI
    implementation(libs.lottie) // Lottie
    implementation(libs.custom.indicator) // Custom Indicator

    // Firebase
    implementation(platform(libs.firebase.bom))
    implementation(libs.firebase.auth)

    //Glide
    implementation(libs.glide)

    //TimeLine
    implementation(libs.timeline)

    // Zxing (Barcode)
    implementation(libs.zxing)

    // Naver Map
    implementation(libs.bundles.naverMap)

    //Speed Dial
    implementation(libs.dial)

    // fcm
    implementation(libs.fcm)
}