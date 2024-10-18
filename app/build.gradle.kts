plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.jetbrains.kotlin.android)
    id("com.google.devtools.ksp")
    id("com.google.dagger.hilt.android")
    alias(libs.plugins.google.gms.google.services)

}

android {
    namespace = "com.Samadhan.livechat"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.Samadhan.livechat"
        minSdk = 26
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

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    implementation(libs.firebase.auth)
    implementation(libs.firebase.firestore)
    implementation(libs.firebase.storage)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)

    implementation(libs.hilt.android)
    ksp (libs.hilt.compiler)
//    for instrumentation tests
    androidTestImplementation(libs.hilt.android.testing)
    kspAndroidTest(libs.hilt.compliler)

//    For Local Unit Testing
    testImplementation(libs.hilt.android.testing)
    kspTest(libs.hilt.compliler)

    val nav_version = "2.8.2"

    implementation("androidx.navigation:navigation-compose:$nav_version")

    implementation("androidx.hilt:hilt-navigation-compose:1.2.0")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-play-services:1.6.1")
    implementation("io.coil-kt:coil-compose:2.4.0")
    implementation ("androidx.datastore:datastore-preferences:1.0.0")
    implementation ("androidx.compose.foundation:foundation:1.5.0")
    implementation ("androidx.compose.material:material:1.5.0")
    implementation ("androidx.compose.ui:ui:1.5.0")
    implementation ("androidx.compose.ui:ui-tooling-preview:1.5.0")


}