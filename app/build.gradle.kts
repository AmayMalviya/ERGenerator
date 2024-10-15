plugins {
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.jetbrainsKotlinAndroid)
    id("kotlin-parcelize")
}

android {
    namespace = "com.example.ergenerator"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.ergenerator"
        minSdk = 21
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            // Make sure these files exist in your project
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
    packagingOptions {
        exclude("META-INF/INDEX.LIST")
        exclude ("META-INF/DEPENDENCIES")
        exclude ("META-INF/NOTICE")
        exclude ("META-INF/LICENS")
        exclude ("META-INF/io.netty.versions.properties")
    }
}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.androidx.appcompat.v170)
    implementation(libs.retrofit)
    implementation(libs.converter.gson)
    implementation(libs.kotlin.stdlib)
    implementation(libs.mpandroidchart)
    implementation(libs.logging.interceptor)
    implementation(libs.androidx.recyclerview)
    implementation (libs.volley)
    implementation(libs.ktor.server.core)
    implementation(libs.ktor.server.netty)
    implementation(libs.mysql.connector.java)
    implementation(libs.ktor.server.html.builder)
    implementation (libs.retrofit)
    implementation (libs.converter.gson)
    implementation(libs.mysql.connector.java.v8031)

    testImplementation(libs.junit)
    testImplementation(libs.junit.jupiter)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
}
