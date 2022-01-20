pluginManagement {
    repositories {
        google()
        gradlePluginPortal()
        mavenCentral()
    }
}

rootProject.name = "bcc-file-mpp"
includeBuild("convention-plugins")
include(":file")