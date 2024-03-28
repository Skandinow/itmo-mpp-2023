import org.jetbrains.kotlin.gradle.plugin.*

plugins {
    kotlin("jvm") version "1.9.10"
    application
}

group = "ru.itmo.mpp"

repositories {
    mavenCentral()
}

java {
}

sourceSets.main {
    java.srcDir("src")
}

application {
    mainClass.set("PossibleExecutionsVerifierKt")
}

tasks["build"].dependsOn("run")