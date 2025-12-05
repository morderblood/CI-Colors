plugins {
    kotlin("jvm") version "2.2.20"
}

group = "org.example"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(kotlin("test"))

    // Color mixing library
    implementation("com.scrtwpns:mixbox:2.0.0")

    // Apache Commons Math for optimization algorithms
    implementation("org.apache.commons:commons-math3:3.6.1")

    implementation("org.jetbrains.kotlin:kotlin-reflect")
}

tasks.test {
    useJUnitPlatform()
}