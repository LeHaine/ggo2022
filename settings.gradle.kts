pluginManagement {
    repositories {
        google()
        gradlePluginPortal()
        mavenCentral()
    }
}

rootProject.name = "littlekt-game-base"
include("game")

enableFeaturePreview("VERSION_CATALOGS")