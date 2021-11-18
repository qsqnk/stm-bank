import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

buildscript {
    dependencies {
        classpath("org.jetbrains.kotlinx:atomicfu-gradle-plugin:0.14.4")
    }
}
apply(plugin = "kotlinx-atomicfu")

plugins {
    kotlin("jvm") version "1.5.31"
    kotlin("plugin.serialization") version "1.5.31"
    application
}

group = "me.qsqnk"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    implementation("com.google.code.gson:gson:2.8.9")
    implementation("io.ktor:ktor-serialization:1.6.5")
    implementation("io.ktor:ktor-server-core:1.6.5")
    implementation("io.ktor:ktor-server-netty:1.6.5")
    implementation("ch.qos.logback:logback-classic:1.2.5")
    implementation("io.ktor:ktor-auth:1.6.5")
    implementation("io.ktor:ktor-locations:1.6.5")
    testImplementation("io.ktor:ktor-server-test-host:1.6.5")
    testImplementation(kotlin("test"))
}

tasks.test {
    useJUnitPlatform()
}

tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "1.8"
}

application {
    mainClass.set("ApplicationKt")
}