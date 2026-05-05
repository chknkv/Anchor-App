description = "KMP entry point: AnchorApp, AnchorViewModel, Koin root module assembly."

plugins {
    alias(libs.plugins.androidKotlinMultiplatformLibrary)
    alias(libs.plugins.composeCompiler)
    alias(libs.plugins.composeMultiplatform)
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.kotlinxSerialization)
}

kotlin {
    android {
        namespace = "com.chknkv.anchor"
        compileSdk = libs.versions.android.compileSdk.get().toInt()
        minSdk = libs.versions.android.minSdk.get().toInt()
        experimentalProperties["android.experimental.kmp.enableAndroidResources"] = true
    }

    listOf(
        iosArm64(),
        iosSimulatorArm64()
    ).forEach { iosTarget ->
        iosTarget.binaries.framework {
            baseName = "AnchorKit"
            isStatic = true
        }
    }

    sourceSets {

        commonMain.dependencies {
            implementation(libs.androidx.lifecycle.runtimeCompose)
            implementation(libs.androidx.lifecycle.viewmodelCompose)
            implementation(libs.components.resources)
            implementation(libs.foundation)
            implementation(libs.koin.compose)
            implementation(libs.koin.compose.viewmodel)
            implementation(libs.koin.core)
            implementation(libs.kotlinx.serialization.json)
            implementation(libs.material3)
            implementation(libs.napier)
            implementation(libs.navigation.compose)
            implementation(libs.runtime)
            implementation(libs.ui)

            implementation(project(":Core:CoreDesignSystem"))
            implementation(project(":Core:CoreNetwork"))
            implementation(project(":Core:CorePasscode"))
            implementation(project(":Core:CoreUtils"))

            implementation(project(":Feature:FeatureMain"))
            implementation(project(":Feature:FeatureWelcome"))
        }

        iosMain.dependencies {
            implementation(libs.foundation)
            implementation(libs.koin.compose)
            implementation(libs.koin.core)
            implementation(libs.material3)
            implementation(libs.napier)
            implementation(libs.runtime)
            implementation(libs.ui)
        }
    }
}
