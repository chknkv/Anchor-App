
description = "Core module with design system."

plugins {
    alias(libs.plugins.androidKotlinMultiplatformLibrary)
    alias(libs.plugins.androidLint)
    alias(libs.plugins.composeCompiler)
    alias(libs.plugins.composeMultiplatform)
    alias(libs.plugins.kotlinMultiplatform)
}

kotlin {
    android {
        namespace = "com.chknkv.coredesignsystem"
        compileSdk = libs.versions.android.compileSdk.get().toInt()
        minSdk = libs.versions.android.minSdk.get().toInt()
        experimentalProperties["android.experimental.kmp.enableAndroidResources"] = true
    }

    iosArm64()
    iosSimulatorArm64()

    sourceSets {
        commonMain.dependencies {
            implementation(libs.components.resources)
            implementation(libs.foundation)
            implementation(libs.kotlin.stdlib)
            implementation(libs.material3)
            implementation(libs.navigation.compose)
            implementation(libs.runtime)
            implementation(libs.ui)

            implementation(project(":Core:CoreUtils"))
        }
    }
}