plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    id("kotlin-kapt") // Esta línea ya la tenías, está perfecta.
}

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
        sourceCompatibility = JavaVersion.VERSION_1_8 // Cambiado a 1.8, es más compatible
        targetCompatibility = JavaVersion.VERSION_1_8 // Cambiado a 1.8
    }
    kotlinOptions {
        jvmTarget = "1.8" // Cambiado a 1.8
    }
}

dependencies {
    // Glide - para cargar imágenes de forma eficiente
    implementation("com.github.bumptech.glide:glide:4.16.0")
    kapt("com.github.bumptech.glide:compiler:4.16.0") // <-- ESTA LÍNEA ES LA QUE FALTA

    // Dependencias existentes...
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

    // --- LÍNEAS PARA LA BASE DE DATOS (ROOM) ---
    val room_version = "2.6.1"
    implementation("androidx.room:room-runtime:$room_version")
    kapt("androidx.room:room-compiler:$room_version")
    implementation("androidx.room:room-ktx:$room_version") // Para usar corutinas
    // ---------------------------------------------------------
}

