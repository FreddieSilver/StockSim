plugins {
    kotlin("jvm") version "1.9.23"
    kotlin("plugin.spring")
    kotlin("plugin.jpa")
    id("org.springframework.boot")
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
    implementation(project(":modules:repository-data"))
    implementation(project(":modules:repository-jpa"))
    implementation(project(":modules:services"))
    implementation(project(":modules:http"))
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("org.springframework.boot:spring-boot-starter")
}

tasks.test {
    useJUnitPlatform()
}

springBoot {
    mainClass.set("org.example.DummyApplication")
}

tasks.bootJar {
    enabled = false
}
