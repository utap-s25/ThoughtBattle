import java.util.Properties

plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("com.google.gms.google-services")
}


val secretsFile = rootProject.file("local.properties")
val properties = Properties()
properties.load(secretsFile.inputStream())

val sendbirdAppId = properties.getProperty("sendbirdAppId")
val wikimediaClientId = properties.getProperty("wikimediaClientId")
val wikimediaClientSecret = properties.getProperty("wikimediaClientSecret")
val wikimediaAccessToken = properties.getProperty("wikimediaAccessToken")

android {
    namespace = "com.example.thoughtbattle"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.thoughtbattle"
        minSdk = 33
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables.useSupportLibrary = true
        android.buildFeatures.buildConfig = true


        buildConfigField("String", "SENDBIRD_APP_ID", sendbirdAppId)
        buildConfigField("String", "WIKIMEDIA_CLIENT_ID", wikimediaClientId)
        buildConfigField("String", "WIKIMEDIA_CLIENT_SECRET", wikimediaClientSecret)
        buildConfigField("String", "WIKIMEDIA_ACCESS_TOKEN", wikimediaAccessToken)

    }

    buildTypes {
        release {
            isMinifyEnabled = true
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
        freeCompilerArgs +=("-Xallow-unstable-dependencies")
    }
    buildFeatures {
        viewBinding = true
    }
}

dependencies {
    // Kotlin
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8:1.9.10")
    implementation(libs.androidx.annotation)
    implementation(libs.androidx.lifecycle.livedata.ktx)
    implementation(libs.androidx.lifecycle.viewmodel.ktx)
    implementation(libs.androidx.runtime.saved.instance.state)
    implementation(libs.androidx.media3.common.ktx)
    implementation ("androidx.activity:activity-ktx:1.8.2")
    implementation(libs.androidx.navigation.fragment.ktx)
    implementation(libs.androidx.navigation.ui.ktx)


    // Testing
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")

    // SendBird
    implementation("com.sendbird.sdk:sendbird-chat:4.24.0")
    implementation ("com.sendbird.sdk:uikit:3.23.0")


    // AndroidX
    implementation("androidx.core:core-ktx:1.12.0")
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    implementation("androidx.recyclerview:recyclerview:1.3.1")
    implementation("com.google.android.material:material:1.9.0")

    // Firebase (BOM)
    implementation(platform("com.google.firebase:firebase-bom:32.3.1"))
    implementation("com.google.firebase:firebase-auth-ktx")
    implementation("com.google.firebase:firebase-firestore-ktx")
    implementation("com.google.firebase:firebase-analytics-ktx")
    implementation("com.google.firebase:firebase-messaging-ktx")
    implementation("com.google.firebase:firebase-auth")
    implementation ("com.google.android.gms:play-services-auth:20.7.0")
    // Import the FirebaseUI
    implementation ("com.firebaseui:firebase-ui-auth:8.0.2")


    // Glide
    implementation("com.github.bumptech.glide:glide:4.15.1")
    annotationProcessor("com.github.bumptech.glide:compiler:4.15.1")

    //Retrofit
    implementation ("com.squareup.retrofit2:retrofit:2.9.0")
    implementation ("com.squareup.retrofit2:converter-gson:2.9.0")
}