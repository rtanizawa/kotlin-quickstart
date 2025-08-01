import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import org.gradle.api.tasks.Copy

plugins {
    id("org.springframework.boot") version "3.2.0"
    id("io.spring.dependency-management") version "1.1.4"
    kotlin("jvm") version "1.9.20"
    kotlin("plugin.spring") version "1.9.20"
    kotlin("plugin.jpa") version "1.9.20"
}

group = "com.example"
version = "0.0.1-SNAPSHOT"

java {
    sourceCompatibility = JavaVersion.VERSION_17
}

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-validation")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
    implementation("org.jetbrains.kotlin:kotlin-reflect")
    implementation("org.postgresql:postgresql")
    implementation("org.flywaydb:flyway-core")
    
    // Kotest dependencies
    testImplementation("io.kotest:kotest-runner-junit5:5.8.0")
    testImplementation("io.kotest:kotest-assertions-core:5.8.0")
    testImplementation("io.kotest:kotest-property:5.8.0")
    testImplementation("io.kotest:kotest-framework-datatest:5.8.0")

    // MockK for mocking
    testImplementation("io.mockk:mockk:1.13.8")
    
    // Spring Boot Test
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("org.testcontainers:postgresql")
    testImplementation("org.testcontainers:junit-jupiter")
}

tasks.withType<KotlinCompile> {
    kotlinOptions {
        freeCompilerArgs += "-Xjsr305=strict"
        jvmTarget = "17"
    }
}

tasks.withType<Test> {
    useJUnitPlatform()
}

tasks.withType<Test>().configureEach {
    useJUnitPlatform()
}

// Configure source sets for integration tests
sourceSets {
    create("integrationTest") {
        compileClasspath += sourceSets.main.get().output
        runtimeClasspath += sourceSets.main.get().output
        kotlin {
            srcDir("src/integrationTest/kotlin")
        }
        resources {
            srcDir("src/integrationTest/resources")
        }
    }
}

// Configure resource processing for integration tests
tasks.named("processIntegrationTestResources", Copy::class.java) {
    duplicatesStrategy = DuplicatesStrategy.INCLUDE
}

// Configure dependencies for integration tests
dependencies {
    "integrationTestImplementation"(sourceSets["test"].output)
    "integrationTestImplementation"("org.postgresql:postgresql")

    "integrationTestImplementation"("org.springframework.boot:spring-boot-starter-web")
    "integrationTestImplementation"("org.springframework.boot:spring-boot-starter-validation")
    "integrationTestImplementation"("org.springframework.boot:spring-boot-starter-data-jpa")
    "integrationTestImplementation"("org.springframework.boot:spring-boot-starter-test")

    "integrationTestImplementation"("org.testcontainers:postgresql")
    "integrationTestImplementation"("org.testcontainers:junit-jupiter")

    "integrationTestImplementation"("io.kotest:kotest-runner-junit5:5.8.0")
    "integrationTestImplementation"("io.kotest:kotest-assertions-core:5.8.0")
    "integrationTestImplementation"("io.kotest:kotest-property:5.8.0")
    "integrationTestImplementation"("io.kotest:kotest-framework-datatest:5.8.0")
    "integrationTestImplementation"("io.kotest.extensions:kotest-extensions-spring:1.1.2")

    "integrationTestImplementation"("io.mockk:mockk:1.13.8")

}

// Configure test tasks to separate unit and integration tests
tasks.register<Test>("unitTest") {
    description = "Runs unit tests (domain classes only)"
    group = "verification"
    
    testClassesDirs = sourceSets["test"].output.classesDirs
    classpath = sourceSets["test"].runtimeClasspath
    
    // Ensure tests are discovered
    testLogging {
        events("passed", "skipped", "failed")
    }
}

tasks.register<Test>("integrationTest") {
    description = "Runs integration tests (persistence and controllers)"
    group = "verification"
    
    testClassesDirs = sourceSets["integrationTest"].output.classesDirs
    classpath = sourceSets["integrationTest"].runtimeClasspath

    // Integration tests depend on the application being built
    dependsOn("testClasses")

    systemProperty("kotest.framework.debug", "true") // enable Kotest debug logs
    systemProperty("kotest.framework.discovery.parallel.enabled", "false")
}

// Make the default test task run only unit tests
tasks.named("test") {
    dependsOn("unitTest")
}

// Add integrationTest to check task
tasks.named("check") {
    dependsOn("integrationTest")
}
