// Ruta: C:/Users/miyuk/AndroidStudioProjects/Proyecto-ServiGo/app/build.gradle.kts

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.ksp)
    id("com.google.gms.google-services")}

android {
    namespace = "com.example.proyectofinal11"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.example.proyectofinal11"
        minSdk = 29
        targetSdk = 36
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
    // --- Dependencias existentes ---
    implementation(libs.glide)
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.androidx.viewpager2)
    implementation(libs.androidx.fragment)
    implementation(libs.play.services.maps)
    implementation(libs.androidx.mediarouter)
    implementation(libs.androidx.recyclerview)
    implementation(libs.androidx.legacy.support.v4)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)

    // --- Dependencias de Room ---
    implementation(libs.androidx.room.runtime)
    implementation(libs.androidx.room.ktx)
    ksp(libs.androidx.room.compiler)

    // --- BLOQUE DE FIREBASE Y GOOGLE CORREGIDO ---
    // 1. Importa la plataforma BoM (Bill of Materials).
    //    Debe ir PRIMERO para gestionar las versiones de las demás librerías de Firebase.
    implementation(platform(libs.firebase.bom))

    // 2. Implementa la librería de autenticación de Firebase usando el alias corregido.
    implementation(libs.firebase.auth)
    implementation(libs.firebase.storage)
    implementation("com.google.firebase:firebase-firestore")
    // 3. Implementa la librería de Google Sign-In.
    implementation(libs.play.services.auth)
    implementation(libs.play.services.maps)
}
