plugins {
    id("java-library")
    id("kotlin")
    id("kotlin-kapt")
//    id "com.github.dcendents.android-maven"
//    id("maven-publish")
}

group = "com.github.MatrixDev"

java {
    sourceCompatibility = JavaVersion.VERSION_1_8
    targetCompatibility = JavaVersion.VERSION_1_8
}

kotlin {
    sourceSets.all {
        languageSettings.apply {
            optIn("com.squareup.kotlinpoet.DelicateKotlinPoetApi")
        }
    }
}

dependencies {
    implementation(project(":composable-routes-lib"))

    // Kotlin runtime
    val kotlinVersion: String by rootProject.extra
    implementation("org.jetbrains.kotlin:kotlin-stdlib:$kotlinVersion")

    // Kotlin code generation
    implementation("com.squareup:kotlinpoet:1.10.2") /*{
        exclude group: "org.jetbrains.kotlin", module: "kotlin-stdlib-jdk8"
    }*/
    implementation("com.squareup:kotlinpoet-metadata:1.10.2")
    implementation("com.squareup:kotlinpoet-metadata-specs:1.9.0")

    // Auto META-INF processor info generator
    //noinspection AnnotationProcessorOnCompilePath
    compileOnly("com.google.auto.service:auto-service:1.0-rc7")

    // Incremental processor support
    compileOnly("net.ltgt.gradle.incap:incap:0.3")
    kapt("net.ltgt.gradle.incap:incap-processor:0.3")
}

/*
publishing {
    publications {
        maven(MavenPublication) {
            groupId "dev.matrix.roomigrant"
            artifactId "roomigrant-compiler"
            version roomigrant_version
            artifact("$buildDir/libs/RoomigrantCompiler.jar")
        }
    }
    repositories {
        maven {
            name = "GitHubPackages"
            url = "https://maven.pkg.github.com/MatrixDev/Roomigrant"
            credentials {
                username = System.getenv("GITHUB_ACTOR")
                password = System.getenv("GITHUB_TOKEN")
            }
        }
    }
}
*/
