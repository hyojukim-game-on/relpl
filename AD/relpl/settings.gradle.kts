pluginManagement {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
        maven("https://naver.jfrog.io/artifactory/maven/")

    }
}

rootProject.name = "Relpl"
include(":app")
include(":presentation")
include(":data")
include(":domain")
include(":retrofit_adapter")
include(":persist_bottomsheet")
include(":btmsheet")
