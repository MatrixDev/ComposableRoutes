plugins {
    id("com.android.application")
    id("kotlin-android")
    id("kotlin-kapt")
    id("kotlin-parcelize")
}

android {
    compileSdk = 31

    defaultConfig {
        applicationId = "dev.matrix.compose_routes"
        minSdk = 21
        targetSdk = 31
        versionCode = 1
        versionName = "1.0"
    }

    buildTypes {
        getByName("release") {
            isMinifyEnabled = false
            proguardFile("proguard-rules.pro")
            proguardFile(getDefaultProguardFile("proguard-android.txt"))
        }
    }

    buildFeatures {
        compose = true
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }

    kotlinOptions {
        jvmTarget = "1.8"
    }

    composeOptions {
        kotlinCompilerExtensionVersion = "1.0.5"
    }
}

dependencies {
    implementation(project(":composable-routes-lib"))
    "kapt"(project(":composable-routes-processor"))

    // AndroidX & Material
    implementation("androidx.core:core-ktx:1.7.0")
    implementation("androidx.appcompat:appcompat:1.3.1")
    implementation("com.google.android.material:material:1.4.0")

    // Compose
    implementation("androidx.compose.ui:ui:1.0.5")
    implementation("androidx.compose.ui:ui-tooling:1.0.5")
    implementation("androidx.compose.foundation:foundation:1.0.5")
    implementation("androidx.compose.runtime:runtime-livedata:1.0.5")
    implementation("androidx.activity:activity-compose:1.4.0")

    // Compose Controls
    implementation("androidx.constraintlayout:constraintlayout-compose:1.0.0-rc01")

    // Compose Material
    implementation("androidx.compose.material:material:1.0.5")
    implementation("androidx.compose.material:material-icons-core:1.0.5")
    implementation("androidx.compose.material:material-icons-extended:1.0.5")

    // Compose Navigation
    implementation("androidx.navigation:navigation-compose:2.4.0-beta02")
}
