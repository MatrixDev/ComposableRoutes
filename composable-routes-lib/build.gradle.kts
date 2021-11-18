plugins {
    id("java-library")
    id("kotlin")
    id("maven-publish")
}

val composeRoutesVersion: String by rootProject.extra
version = composeRoutesVersion
group = "com.github.MatrixDev.ComposableRoutes"

java {
    sourceCompatibility = JavaVersion.VERSION_1_8
    targetCompatibility = JavaVersion.VERSION_1_8
}
