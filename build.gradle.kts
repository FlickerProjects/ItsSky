plugins {
    `java-library`
    `maven-publish`
    id("io.izzel.taboolib") version "1.51"
    id("org.jetbrains.kotlin.jvm") version "1.5.31"
}

taboolib {
    description {
        dependencies {
            name("Realms")
        }
    }
    install("common", "common-5")
    install("module-configuration")
    install("expansion-command-helper")
    install("platform-bukkit")
    classifier = null
    version = "6.0.10-31"
}

repositories {
    mavenCentral()
}

dependencies {
    compileOnly("ink.ptms.core:v11900:11900:mapped")
    compileOnly("ink.ptms.core:v11900:11900:universal")
    compileOnly("ink.ptms:nms-all:1.0.0")

    compileOnly(kotlin("stdlib"))
    compileOnly(fileTree("libs"))
}

tasks.withType<JavaCompile> {
    options.encoding = "UTF-8"
}

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
    kotlinOptions {
        jvmTarget = "1.8"
        freeCompilerArgs = listOf("-Xjvm-default=all")
    }
}

configure<JavaPluginConvention> {
    sourceCompatibility = JavaVersion.VERSION_1_8
    targetCompatibility = JavaVersion.VERSION_1_8
}