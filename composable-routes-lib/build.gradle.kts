plugins {
    id("java-library")
    id("kotlin")
    id("maven-publish")
}

java {
    sourceCompatibility = JavaVersion.VERSION_1_8
    targetCompatibility = JavaVersion.VERSION_1_8
}

publishing {
    val composeRoutesVersion: String by rootProject.extra
    publications {
        create("maven", MavenPublication::class) {
            groupId = "dev.matrix.composable-routes"
            artifactId = "composable-routes-lib"
            version = composeRoutesVersion
            artifact("$buildDir/libs/composable-routes-lib.jar")
        }
    }
    repositories {
        maven {
            name = "GitHubPackages"
            setUrl("https://maven.pkg.github.com/MatrixDev/ComposableRoutes")
            credentials {
                username = System.getenv("GITHUB_ACTOR")
                password = System.getenv("GITHUB_TOKEN")
            }
        }
    }
}
