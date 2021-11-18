buildscript {
    val kotlinVersion by extra { "1.5.31" }
    val composeRoutesVersion by extra { "0.1.0" }

    repositories {
        google()
        mavenCentral()
    }

    dependencies {
        classpath("com.android.tools.build:gradle:7.0.3")
        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:$kotlinVersion")
//        classpath("com.github.dcendents:android-maven-gradle-plugin:2.1")
    }
}

tasks.register("clean", Delete::class) {
    delete(rootProject.buildDir)
    rootProject.childProjects.forEach { project ->
        delete(project.value.buildDir)
    }
}
