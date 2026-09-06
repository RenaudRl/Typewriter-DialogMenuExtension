plugins {
    kotlin("jvm") version "2.2.10"
    id("com.typewritermc.module-plugin") version "2.2.0"
}

group = "btcrenaud"
version = "0.0.7"

repositories {
    mavenCentral()
    maven("https://repo.papermc.io/repository/maven-public/")
    maven("https://maven.typewritermc.com/beta/")
    maven("https://maven.typewritermc.com/external/")
}

dependencies {
    compileOnly("io.papermc.paper:paper-api:1.21.11-R0.1-SNAPSHOT")

    implementation("com.typewritermc:BasicExtension:0.9.0")
}

typewriter {
    namespace = "btcrenaud"
    extension {
        name = "DialogMenu"
        shortDescription = "Dialog menu system for TypeWriter"
        description = "Dialog menu extension for TypeWriter providing advanced dialog menus with branching conversations and interactive NPC dialogue."
        engineVersion = "0.9.0-beta-176"
        channel = com.typewritermc.moduleplugin.ReleaseChannel.BETA
        paper()
        dependencies {}
    }
}

kotlin {
    jvmToolchain(21)
}
