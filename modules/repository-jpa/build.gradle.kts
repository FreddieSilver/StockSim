plugins {
    kotlin("jvm") version "1.9.23"
    kotlin("plugin.spring")
    kotlin("plugin.jpa")
}

group = "org.example"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(kotlin("test"))

    implementation(project(":modules:domain"))
    implementation(project(":modules:repository"))
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    runtimeOnly("org.postgresql:postgresql")
}

tasks.test {
    useJUnitPlatform()
}
