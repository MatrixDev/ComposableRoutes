plugins {
    id("java-library")
    id("kotlin")
    id("kotlin-kapt")
    id("maven-publish")
}

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

publishing {
    val composeRoutesVersion: String by rootProject.extra
    publications {
        create<MavenPublication>("maven") {
            groupId = "dev.matrix.composable-routes"
            artifactId = "composable-routes-processor"
            version = composeRoutesVersion
            from(components.getByName("java"))
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
