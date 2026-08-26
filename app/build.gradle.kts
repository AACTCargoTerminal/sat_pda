import java.util.Properties

val env = Properties().apply {
    rootProject.file("env.properties")
        .reader(Charsets.UTF_8)
        .use { load(it) }
}

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    kotlin("plugin.serialization") version "2.0.21"
    id("kotlin-kapt")
}

android {
    namespace = "com.aact.sat_pda"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.aact.sat_pda"
        minSdk = 29
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables.useSupportLibrary = true

        buildConfigField("String", "SERVER_LIST", "\"${env["SERVER_LIST"]}\"")
        buildConfigField("String", "DEFAULT_SERVER_IP", "\"${env["DEFAULT_SERVER_IP"]}\"")
        buildConfigField("String", "SERVER_KEY", "\"${env["SERVER_KEY"]}\"")
        buildConfigField("long", "SERVER_TIMEOUT", "${env["SERVER_TIMEOUT"]}L")
        buildConfigField("String", "PROGRAM_ID", "\"${env["PROGRAM_ID"]}\"")
        buildConfigField("String", "PDA_VERSION", "\"${env["PDA_VERSION"]}\"")
        buildConfigField("String", "UPDATE_SERVER_IP", "\"${env["UPDATE_SERVER_IP"]}\"")
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
        compose = true
        viewBinding = true
        dataBinding = true
        buildConfig = true
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
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)
    implementation("androidx.constraintlayout:constraintlayout:2.0.4")
    implementation("com.google.android.material:material:1.12.0")
    implementation("com.squareup.okhttp3:okhttp:4.9.3")
    val nav_version = "2.9.0"
    implementation("androidx.navigation:navigation-fragment:$nav_version")
    implementation("androidx.navigation:navigation-ui:$nav_version")
    implementation("androidx.navigation:navigation-compose:${nav_version}")
    implementation("androidx.compose.ui:ui:1.6.6")
    implementation("androidx.compose.material3:material3:1.2.1")
    implementation("androidx.activity:activity-compose:1.9.0")
    implementation("com.google.accompanist:accompanist-permissions:0.34.0")
    implementation("androidx.databinding:databinding-runtime:8.11.0")
    val camerax_version = "1.4.1"
    implementation("androidx.camera:camera-core:$camerax_version")
    implementation("androidx.camera:camera-camera2:$camerax_version")
    implementation("androidx.camera:camera-lifecycle:$camerax_version")
    implementation("androidx.camera:camera-view:$camerax_version")
    implementation("com.google.code.gson:gson:2.10.1")
    implementation("androidx.activity:activity-ktx:1.8.2")
    implementation("androidx.work:work-runtime-ktx:2.9.0")

}