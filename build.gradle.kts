plugins {
    kotlin("jvm") version "2.3.20"
    id("com.typewritermc.module-plugin") version "2.1.0"
}
group = "btcrenaud"
version = "0.0.5"

repositories {
    mavenLocal()
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
        engineVersion = "0.9.0-beta-174"
        channel = com.typewritermc.moduleplugin.ReleaseChannel.BETA
        paper()
        dependencies {}
    }
}

    

kotlin {
    jvmToolchain(25)
    
}
