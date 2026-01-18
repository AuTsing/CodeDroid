import com.android.build.gradle.internal.api.BaseVariantOutputImpl
import com.android.build.gradle.internal.cxx.configure.gradleLocalProperties

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
}

android {
    namespace = "com.autsing.codedroid"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.autsing.codedroid"
        minSdk = 26
        targetSdk = 36
        versionCode = 20102
        versionName = "2.1.2"
    }

    signingConfigs {
        getByName("debug") {
            val properties = gradleLocalProperties(rootDir, providers)
            storeFile = file(properties.getProperty("STORE_FILE"))
            storePassword = properties.getProperty("STORE_PASSWORD")
            keyAlias = properties.getProperty("KEY_ALIAS")
            keyPassword = properties.getProperty("KEY_PASSWORD")
        }
        create("release") {
            val properties = gradleLocalProperties(rootDir, providers)
            storeFile = file(properties.getProperty("STORE_FILE"))
            storePassword = properties.getProperty("STORE_PASSWORD")
            keyAlias = properties.getProperty("KEY_ALIAS")
            keyPassword = properties.getProperty("KEY_PASSWORD")
        }
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
            signingConfig = signingConfigs.getByName("release")
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    kotlinOptions {
        jvmTarget = "17"
    }

    applicationVariants.all {
        outputs.map { it as BaseVariantOutputImpl }
            .forEach {
                it.outputFileName = "${rootProject.name}_v${versionName}_${buildType.name}.apk"
            }
    }
}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.agentweb.core)
    implementation("androidx.datastore:datastore-preferences:1.2.0")
}
