plugins {
    id("org.jetbrains.kotlin.jvm") version "1.8.10"

    `java-library`

    kotlin("plugin.serialization") version "1.4.31"
}

repositories {
    mavenCentral()
}

dependencies {
    testImplementation("org.jetbrains.kotlin:kotlin-test-junit5")

    testImplementation("org.junit.jupiter:junit-jupiter-engine:5.9.1")

    api("org.apache.commons:commons-math3:3.6.1")

    implementation("com.google.guava:guava:31.1-jre")

    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.5.0")

    implementation("com.github.ajalt.clikt:clikt:3.5.2")

    val ktorVersion = "2.2.4"
    implementation("io.ktor:ktor-client-core:$ktorVersion")
    implementation("io.ktor:ktor-client-cio:$ktorVersion")
    implementation("io.ktor:ktor-serialization-kotlinx-json:$ktorVersion")
    implementation("io.ktor:ktor-client-content-negotiation:$ktorVersion")
    implementation("io.ktor:ktor-client-logging:$ktorVersion")
    testImplementation("io.ktor:ktor-client-mock:$ktorVersion")
}

tasks.named<Test>("test") {
    useJUnitPlatform()
}

