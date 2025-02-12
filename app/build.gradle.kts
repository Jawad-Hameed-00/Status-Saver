plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
}

android {
    namespace = "com.jawadjatoi.statussaver"
    compileSdk = 35

    buildFeatures{
        viewBinding = true
    }
    defaultConfig {
        applicationId = "com.jawadjatoi.statussaver"
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
    kotlinOptions {
        jvmTarget = "11"
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

    implementation ("com.jsibbold:zoomage:1.3.1")
    implementation ("androidx.media3:media3-exoplayer:1.1.1")
    implementation ("androidx.media3:media3-exoplayer-dash:1.1.1")
    implementation ("androidx.media3:media3-ui:1.1.1")

    implementation ("androidx.documentfile:documentfile:1.0.1")
    implementation ("com.github.bumptech.glide:glide:4.16.0")
    implementation("androidx.swiperefreshlayout:swiperefreshlayout:1.2.0-alpha01")
    implementation ("com.anggrayudi:storage:1.5.5")
}