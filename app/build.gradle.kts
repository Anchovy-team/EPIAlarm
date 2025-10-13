import java.util.Properties

val localProperties = Properties()
val keyStoreProperties = Properties()

val localPropertiesFile = rootProject.file("local.properties")
val keyStorePropertiesFile = rootProject.file("keystore.properties")

if (localPropertiesFile.exists()) {
    localProperties.load(localPropertiesFile.inputStream())
}

if (keyStorePropertiesFile.exists()){
    keyStoreProperties.load(keyStorePropertiesFile.inputStream())
}

plugins {
    alias(libs.plugins.android.application)
}

android {
    signingConfigs {
        getByName("debug") {
            storeFile = keyStoreProperties["storeFile"]?.let { file(it as String) }
            storePassword = keyStoreProperties["storePassword"] as String?
            keyAlias = keyStoreProperties["keyAlias"] as String?
            keyPassword = keyStoreProperties["keyPassword"] as String?
        }
    }

    namespace = "anchovy.team.epialarm"
    compileSdk = 35

    defaultConfig {
        manifestPlaceholders += mapOf()
        applicationId = "anchovy.team.epialarm"
        minSdk = 26
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        signingConfig = signingConfigs.getByName("debug")

        manifestPlaceholders["msalHost"] = localProperties.getProperty("MSAL_HOST")
        manifestPlaceholders["msalPath"] = localProperties.getProperty("MSAL_PATH")
    }

    buildFeatures {
        viewBinding = true
    }

    buildTypes {
        getByName("debug") {
            signingConfig = signingConfigs.getByName("debug")
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
    implementation(libs.ext.junit)
    implementation(libs.firebase.crashlytics.buildtools)
    implementation(libs.firebase.perf)
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
    // https://mvnrepository.com/artifact/com.squareup.okhttp3/okhttp
    implementation("com.squareup.okhttp3:okhttp:4.12.0")
    // https://mvnrepository.com/artifact/com.fasterxml.jackson.core/jackson-databind

    implementation("com.fasterxml.jackson.dataformat:jackson-dataformat-xml:2.15.2")
    implementation("com.fasterxml.jackson.core:jackson-databind:2.15.2")
    implementation("com.fasterxml.jackson.datatype:jackson-datatype-jsr310:2.15.2")
    implementation("io.github.cdimascio:dotenv-java:3.0.0")

    implementation ("com.microsoft.identity.client:msal:5.1.0")
    implementation ("com.android.volley:volley:1.2.1")
    implementation ("androidx.work:work-runtime:2.10.4")
}
