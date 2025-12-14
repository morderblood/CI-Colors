plugins {
    kotlin("jvm") version "2.2.20"
}

group = "com.github.morderblood"
version = "1.0.0"

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(kotlin("test"))

    // Color mixing library
    implementation("com.scrtwpns:mixbox:2.0.0")

    // Apache Commons Math for optimization algorithms
    implementation("org.apache.commons:commons-math3:3.6.1")

    implementation("org.moeaframework:moeaframework:5.1")
    implementation(kotlin("reflect"))
}

tasks.test {
    useJUnitPlatform()
}