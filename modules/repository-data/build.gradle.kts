plugins {
    kotlin("jvm") version "1.9.23"
}

group = "dev.freddiesilver.stocksim"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(kotlin("test"))
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")

    implementation(project(":modules:domain"))
    implementation(project(":modules:repository"))
}

tasks.test {
    useJUnitPlatform()
}