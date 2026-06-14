plugins {
    kotlin("jvm") version "1.9.23"
    kotlin("plugin.spring")
}

group = "org.example"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(kotlin("test"))

    implementation(project(":modules:domain"))
    implementation(project(":modules:services"))
    implementation("org.springframework.boot:spring-boot-starter-web")
}

tasks.test {
    useJUnitPlatform()
}