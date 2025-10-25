import java.util.Properties

val localProperties = Properties()
val keyStorePropertiesDebug = Properties()
val keyStoreProperties = Properties()

val localPropertiesFile = rootProject.file("local.properties")
val keyStorePropertiesDebugFile = rootProject.file("keystore.properties")
val keyStorePropertiesFile = rootProject.file("keystore.properties.release")

if (localPropertiesFile.exists()) {
    localProperties.load(localPropertiesFile.inputStream())
}

if (keyStorePropertiesDebugFile.exists()){
    keyStorePropertiesDebug.load(keyStorePropertiesDebugFile.inputStream())
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
            storeFile = keyStorePropertiesDebug["storeFile"]?.let { file(it as String) }
            storePassword = keyStorePropertiesDebug["storePassword"] as String?
            keyAlias = keyStorePropertiesDebug["keyAlias"] as String?
            keyPassword = keyStorePropertiesDebug["keyPassword"] as String?
        }
        create("release") {
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
        versionCode = 4
        versionName = "0.1.3"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        signingConfig = signingConfigs.getByName("debug")
        val isBundleBuild = gradle.startParameter.taskNames.any { it.contains("bundle", ignoreCase = true) }
        val selectedSignature = if (isBundleBuild) localProperties.getProperty("MSAL_PATH_BUNDLE") else localProperties.getProperty("MSAL_PATH")

        manifestPlaceholders["msalHost"] = localProperties.getProperty("MSAL_HOST")
        manifestPlaceholders["msalPath"] = selectedSignature
    }

    buildFeatures {
        viewBinding = true
        buildConfig = true
    }

    buildTypes {
        getByName("debug") {
            signingConfig = signingConfigs.getByName("debug")
        }
        getByName("release") {
            isMinifyEnabled = false
            isShrinkResources = false
            signingConfig = signingConfigs.getByName("release")
            enableAndroidTestCoverage = false
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

    implementation("com.squareup.okhttp3:okhttp:4.12.0")

    implementation("com.fasterxml.jackson.dataformat:jackson-dataformat-xml:2.15.2")
    implementation("com.fasterxml.jackson.core:jackson-databind:2.15.2")
    implementation("com.fasterxml.jackson.datatype:jackson-datatype-jsr310:2.15.2")
    implementation("io.github.cdimascio:dotenv-java:3.0.0")

    implementation ("com.microsoft.identity.client:msal:5.1.0")
    implementation ("com.android.volley:volley:1.2.1")
    implementation ("androidx.work:work-runtime:2.10.4")
}

afterEvaluate {
    val isBundleBuild = gradle.startParameter.taskNames.any { it.contains("bundle", ignoreCase = true) }

    val sourceFile = if (isBundleBuild) {
        file("src/main/res/raw/auth_config_bundle.json")
    } else {
        file("src/main/res/raw/auth_config_apk.json")
    }

    val destinationDir = file("src/main/res/raw")
    val destinationFileName = "auth_config_single_account.json"

    android.applicationVariants.all {
        val variantName = name.replaceFirstChar { it.uppercase() }
        val copyTaskName = "copyAuthConfigFor$variantName"

        tasks.register<Copy>(copyTaskName) {
            from(sourceFile)
            into(destinationDir)
            rename { destinationFileName }
        }

        preBuild.dependsOn(copyTaskName)
    }
}