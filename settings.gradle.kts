rootProject.name = "Anchor-App"
enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")

pluginManagement {
    repositories {
        google {
            mavenContent {
                includeGroupAndSubgroups("androidx")
                includeGroupAndSubgroups("com.android")
                includeGroupAndSubgroups("com.google")
            }
        }
        mavenCentral()
        gradlePluginPortal()
    }
}

dependencyResolutionManagement {
    repositories {
        google {
            mavenContent {
                includeGroupAndSubgroups("androidx")
                includeGroupAndSubgroups("com.android")
                includeGroupAndSubgroups("com.google")
            }
        }
        mavenCentral()
    }
}

// Multiplatform Region
include(":Anchor-MobileApp:androidApp")
include(":Anchor-MobileApp:shared")
// END Multiplatform Region

// Core Region
include(":Core:CoreDesignSystem")
include(":Core:CoreUtils")
include(":Core:CorePasscode")
include(":Core:CoreNetwork")
// END Core Region

// Feature Region
include(":Feature:FeatureMain")
include(":Feature:FeatureWelcome")
include(":Feature:FeatureSettings")
include(":Feature:FeatureAddiction")
include(":Feature:FeatureAssistant")
// END Feature Region
