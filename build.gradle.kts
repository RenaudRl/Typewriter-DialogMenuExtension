plugins {
    kotlin("jvm") version "2.2.10"
    id("com.typewritermc.module-plugin") version "2.1.0"
}

group = "btc.renaud"
version = "0.1" // The version is the same with the plugin to avoid confusion. :)

repositories {
    maven("https://repo.papermc.io/repository/maven-public/")
    maven("https://maven.enginehub.org/repo/")
    flatDir {
        dir("libs")
    }
}

dependencies {
    implementation("com.typewritermc:QuestExtension:0.9.0")
    implementation("com.typewritermc:BasicExtension:0.9.0")
    // Use Paper 1.21.7 which provides the Dialog API used by this extension
    compileOnly("io.papermc.paper:paper-api:1.21.8-R0.1-SNAPSHOT")
}

typewriter {
    namespace = "renaud"

    extension {
        name = "DialogMenu"
        shortDescription = "Typewriter extension For DialogMenu support."
        description =
            "This extension adds support for GUI and Dialog (1.21.6 dialog system) to typewriter for create" +
            " easy different type of menus."
        engineVersion = "0.9.0-beta-171"
        channel = com.typewritermc.moduleplugin.ReleaseChannel.BETA
        dependencies {
            dependency("typewritermc", "Quest")
        }
        paper()

    }

}

kotlin {
    jvmToolchain(21)
    compilerOptions {
        jvmTarget.set(org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_21)
    }
}



