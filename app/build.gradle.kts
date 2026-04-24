plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.ksp)
    alias(libs.plugins.google.services)
    alias(libs.plugins.hilt)
}

android {
    namespace = "com.focus.mob"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.focus.mob"
        minSdk = 31
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
    kotlinOptions {
        jvmTarget = "11"
    }
    buildFeatures {
        viewBinding = true
    }
}

dependencies {

    // ─── AndroidX Core ────────────────────────────────────────────
    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity.ktx)
    implementation(libs.constraintlayout)

    // ─── Room (local DB) ──────────────────────────────────────────
    implementation(libs.room.runtime)
    implementation(libs.room.ktx)
    ksp(libs.room.compiler)

    // ─── Coroutines + Lifecycle ───────────────────────────────────
    implementation(libs.coroutines.android)
    implementation(libs.lifecycle.runtime)
    implementation(libs.lifecycle.viewmodel)

    // ─── Firebase ────────────────────────────────────────────────
    implementation(platform(libs.firebase.bom))
    implementation(libs.firebase.auth)

    // ─── Google Sign-In (Credential Manager) ─────────────────────
    implementation(libs.credentials)
    implementation(libs.credentials.play)
    implementation(libs.googleid)

    // ════════════════════════════════════════════════════════════
    //                    PRO STACK UPGRADE
    // ════════════════════════════════════════════════════════════

    // ─── Hilt (Dependency Injection) ──────────────────────────────
    implementation(libs.hilt.android)
    ksp(libs.hilt.compiler)

    // ─── Retrofit + OkHttp (Networking) ───────────────────────────
    implementation(libs.retrofit)
    implementation(libs.retrofit.gson)
    implementation(libs.okhttp.logging)

    // ─── ExoPlayer / Media3 (Audio streaming) ─────────────────────
    implementation(libs.exoplayer)
    implementation(libs.exoplayer.ui)
    implementation(libs.exoplayer.dash)

    // ─── Timber (Logging) ─────────────────────────────────────────
    implementation(libs.timber)

    // ─── Shimmer (Loading states) ─────────────────────────────────
    implementation(libs.shimmer)

    // ─── WorkManager (Background tasks / reminders) ───────────────
    implementation(libs.workmanager)

    // ─── MPAndroidChart (Stats charts) ────────────────────────────
    implementation(libs.mpandroidchart)

    // ─── Tests ────────────────────────────────────────────────────
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
}