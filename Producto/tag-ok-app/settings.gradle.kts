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
        // Mapbox SDK — requiere SDK_REGISTRY_TOKEN (token secreto sk.).
        // Orden: local.properties (por proyecto) -> gradle.properties global
        // (~/.gradle/gradle.properties, una sola vez por máquina, nunca en el repo).
        val sdkRegistryToken = localProps.getProperty("SDK_REGISTRY_TOKEN")
            ?: providers.gradleProperty("SDK_REGISTRY_TOKEN").orNull
            ?: ""
        maven {
            url = uri("https://api.mapbox.com/downloads/v2/releases/maven")
            authentication { create<HttpHeaderAuthentication>("header") }
            credentials(HttpHeaderCredentials::class) {
                name = "Authorization"
                value = "Token $sdkRegistryToken"
            }
        }
    }
}

rootProject.name = "TagOkApp"
include(":app")
