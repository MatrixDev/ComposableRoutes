dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
    }
}

rootProject.name = "composable-routes"

include(":composable-routes-test")
include(":composable-routes-lib")
include(":composable-routes-compiler")
