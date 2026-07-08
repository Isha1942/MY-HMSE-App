plugins {
    id("com.android.application")
    alias(libs.plugins.google.gms.google.services)
}

android {
    namespace = "com.spark.newapp2"
    compileSdk = 35   // Updated to support newer libraries

    defaultConfig {
        applicationId = "com.spark.newapp2"
        minSdk = 24
        targetSdk = 34 // Keep target stable for compatibility
        versionCode = 2   // Increase this every time you rebuild
        versionName = "1.1"
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        getByName("debug") { }
        getByName("release") {
            isMinifyEnabled = false
            // optional: add proguard rules later
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
}

// Deprecated API warnings
tasks.withType<JavaCompile> {
    options.compilerArgs.add("-Xlint:deprecation")
}

dependencies {
    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)

    // Room database
    implementation("androidx.room:room-runtime:2.6.0")
    annotationProcessor("androidx.room:room-compiler:2.6.0")

    // Firebase Auth
    implementation(libs.firebase.auth)

    // Tests
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
}
