dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
        maven(url = "https://jitpack.io")
    }
}

rootProject.name = "composable-routes"

include(":composable-routes-test")
include(":composable-routes-lib")
include(":composable-routes-processor")
