plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.google.services)
}

android {
    namespace = "com.example.myapplication"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.myapplication"
        minSdk = 23
        targetSdk = 35
        versionCode = 1
        multiDexEnabled = true
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
}

dependencies {

    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)
    implementation(libs.firebase.auth)
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
    implementation("com.google.firebase:firebase-bom:33.12.0")
    implementation ("com.google.firebase:firebase-auth")
    implementation ("com.google.android.gms:play-services-auth:19.0.0")
    implementation ("androidx.recyclerview:recyclerview:1.1.0")
    implementation ("androidx.recyclerview:recyclerview-selection:1.1.0")
    implementation ("androidx.cardview:cardview:1.0.0")
    implementation ("com.google.android.material:material:1.11.0")
    implementation("com.google.firebase:firebase-firestore:25.1.4")
    implementation("com.android.support:multidex:1.0.3")
}