plugins {
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.kotlinAndroid)
    alias(libs.plugins.ksp)
}

android {
    namespace = "com.vimal.margh"
    compileSdk = 35
    defaultConfig {
        applicationId = "com.vimal.margh"
        minSdk = 24
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = true
            isShrinkResources = true
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
    buildFeatures {
        viewBinding = true
        buildConfig = true
    }

}

dependencies {
    annotationProcessor(libs.compiler)
    implementation(libs.appcompat)
    implementation(libs.constraintlayout)
    implementation(libs.converter.gson)
    implementation(libs.exoplayer)
    implementation(libs.glide)
    implementation(libs.lifecycle.livedata.ktx)
    implementation(libs.lifecycle.service)
    implementation(libs.lifecycle.viewmodel.ktx)
    implementation(libs.logging.interceptor)
    implementation(libs.material)
    implementation(libs.media3.common)
    implementation(libs.media3.exoplayer)
    implementation(libs.media3.exoplayer.dash)
    implementation(libs.media3.session)
    implementation(libs.media3.ui)
    implementation(libs.navigation.fragment)
    implementation(libs.navigation.ui)
    implementation(libs.okhttp)
    implementation(libs.picasso)
    implementation(libs.retrofit)
    implementation(libs.room.runtime)
    implementation(libs.swiperefreshlayout)
    ksp(libs.room.compiler)
}
