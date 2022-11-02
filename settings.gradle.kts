pluginManagement {
    repositories {
        google()
        gradlePluginPortal()
        mavenCentral()
    }
}

rootProject.name = "glutton-for-punishment"
include("game")

enableFeaturePreview("VERSION_CATALOGS")