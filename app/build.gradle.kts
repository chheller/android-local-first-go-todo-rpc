plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    kotlin("plugin.serialization") version "1.9.0"
    id("com.google.dagger.hilt.android")
    id("kotlin-kapt")
}

android {
    namespace = "me.charleshh.gorpctodo"
    compileSdk = 34

    defaultConfig {
        applicationId = "me.charleshh.gorpctodo"
        minSdk = 34
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }
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
    buildFeatures {
        compose = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.1"
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}
// Allow references to generated code
kapt {
    correctErrorTypes = true
}
dependencies {
    val room_version = "2.6.1"
    val work_version = "2.9.1"
    // note: this is currently only available in the local maven repository by running
    // gradle publishToMavenLocal from https://github.com/chheller/go-rpc-todo
    implementation("com.charleshh:go-rpc-todo:1.0.0")
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)
    // Deps for navigation
    implementation("androidx.navigation:navigation-compose:2.8.0-rc01")
    // Serialization for navigation
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.6.3")
    // Deps for Hilt
    implementation("com.google.dagger:hilt-android:2.50")
    kapt("com.google.dagger:hilt-android-compiler:2.50")
    implementation("androidx.hilt:hilt-navigation-compose:1.0.0")
    // Deps for Room
    implementation("androidx.room:room-runtime:$room_version")
    annotationProcessor("androidx.room:room-compiler:$room_version")
    // To use Kotlin annotation processing tool (kapt)
    kapt("androidx.room:room-compiler:$room_version")
    // optional - Kotlin Extensions and Coroutines support for Room
    implementation("androidx.room:room-ktx:$room_version")
    // optional - Test helpers
    testImplementation("androidx.room:room-testing:$room_version")
    // UUIDv7
    implementation("com.aallam.ulid:ulid-kotlin:1.3.0")
    // Work Manager
    implementation("androidx.work:work-runtime-ktx:$work_version")
    implementation("androidx.work:work-gcm:$work_version")
    androidTestImplementation("androidx.work:work-testing:$work_version")
    implementation("androidx.work:work-multiprocess:$work_version")
}