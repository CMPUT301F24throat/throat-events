plugins {
    alias(libs.plugins.android.application)
    id("com.google.gms.google-services")
}

android {
    namespace = "com.example.pickme"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.pickme"
        minSdk = 26
        targetSdk = 34
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

    buildFeatures{
        viewBinding = true
    }
}

dependencies {

    // Core Android Libraries
    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)

    // Firebase Services
    implementation(libs.firebase.inappmessaging)
    implementation(libs.firebase.database)
    implementation(platform(libs.firebase.bom))
    implementation(libs.firebase.firestore)
    implementation(libs.firebase.storage)
    implementation(libs.firebase.auth)

    // Image Handling Libraries
    implementation(libs.glide)
    implementation(libs.circleimageview)

    // Navigation Components
    implementation("androidx.navigation:navigation-fragment-ktx:2.8.3")
    implementation("androidx.navigation:navigation-ui-ktx:2.8.3")

    // Barcode Scanning Libraries
    implementation(libs.zxing.core.v350)
    implementation(libs.javase)
    implementation(libs.zxing.android)

    // Play Services for Background Tasks
    implementation("com.google.android.gms:play-services-tasks:18.2.0")

    // Firebase Bills of Materials for Version Alignment
    implementation(platform("com.google.firebase:firebase-bom:33.5.1"))
    implementation("com.google.firebase:firebase-firestore:25.1.1")
    implementation("com.google.firebase:firebase-auth:version")
    implementation("com.google.firebase:firebase-firestore:version")

    // Testing Libraries
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
    androidTestImplementation(libs.navigation.testing)

    val nav_version = "2.8.3"

    // Jetpack Compose Integration
    implementation(libs.navigation.compose)

    // Views/Fragments Integration
    implementation(libs.navigation.fragment)
    implementation(libs.navigation.ui)

    // Feature module support for Fragments
    implementation(libs.navigation.dynamic.features.fragment)

    // Noinspection GradleDependency
    implementation (libs.glide)
    annotationProcessor (libs.compiler)
}