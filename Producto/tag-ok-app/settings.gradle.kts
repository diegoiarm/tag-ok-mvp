import java.util.Properties

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

val localProps = Properties().apply {
    val f = file("local.properties")
    if (f.exists()) f.inputStream().use { load(it) }
}

dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
        // Mapbox SDK — requiere SDK_REGISTRY_TOKEN en local.properties
        maven {
            url = uri("https://api.mapbox.com/downloads/v2/releases/maven")
            authentication { create<HttpHeaderAuthentication>("header") }
            credentials(HttpHeaderCredentials::class) {
                name = "Authorization"
                value = "Token ${localProps.getProperty("SDK_REGISTRY_TOKEN", "")}"
            }
        }
    }
}

rootProject.name = "TagOkApp"
include(":app")
