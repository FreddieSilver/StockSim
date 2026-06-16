plugins {
    id("org.springframework.boot") version "3.2.4"
    id("io.spring.dependency-management") version "1.1.5"

    kotlin("jvm") version "1.9.23"

    // spring
    kotlin("plugin.spring") version "1.9.23"

    // jpa
    kotlin("plugin.jpa") version "1.9.23"

    // ktlint
    id("org.jlleitschuh.gradle.ktlint") version "12.1.0"
}

group = "org.example"
version = "1.0-SNAPSHOT"

allprojects {
    repositories {
        mavenCentral()
    }
}

subprojects {
    apply(plugin = "io.spring.dependency-management")
    apply(plugin = "org.jlleitschuh.gradle.ktlint")
    dependencyManagement {
        imports {
            mavenBom("org.springframework.boot:spring-boot-dependencies:3.2.4")
        }
    }
}

dependencies {
    // spring boot
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-validation")
    implementation(
        "com.fasterxml.jackson.module:jackson-module-kotlin",
    ) // support for Kotlin data classes in JSON serialization/deserialization
    implementation("org.jetbrains.kotlin:kotlin-reflect") // support for reflection (needed for Spring Boot)

    // JPA
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    runtimeOnly("org.postgresql:postgresql") // database

    // UI at http://localhost:8080/swagger-ui.html
    implementation("org.springdoc:springdoc-openapi-starter-webmvc-ui:2.4.0")

    // testing
    testImplementation(kotlin("test"))
    testImplementation("org.springframework.boot:spring-boot-starter-test")
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
