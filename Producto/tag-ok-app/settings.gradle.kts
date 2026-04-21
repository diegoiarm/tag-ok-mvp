pluginManagement {
    repositories {
        google {
            content {
                includeGroupByRegex("com\\.android.*")
                includeGroupByRegex("com\\.google.*")
                includeGroupByRegex("androidx.*")
            }
        }
        mavenCentral()
        gradlePluginPortal()
    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
        // Mapbox SDK — requiere SDK_REGISTRY_TOKEN en ~/.gradle/gradle.properties
        maven {
            url = uri("https://api.mapbox.com/downloads/v2/releases/maven")
            authentication { create<HttpHeaderAuthentication>("header") }
            credentials(HttpHeaderCredentials::class) {
                name = "Authorization"
                value = "Token ${providers.gradleProperty("SDK_REGISTRY_TOKEN").getOrElse("")}"
            }
        }
    }
}

rootProject.name = "TagOkApp"
include(":app")
