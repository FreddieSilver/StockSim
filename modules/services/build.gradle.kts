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
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")

    implementation(project(":modules:domain"))
    implementation(project(":modules:repository"))
    implementation(project(":modules:repository-data"))
    implementation("org.springframework.boot:spring-boot-starter")
}

tasks.test {
    useJUnitPlatform()
}