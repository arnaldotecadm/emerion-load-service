plugins {
    kotlin("jvm") version "2.2.21"
    kotlin("plugin.spring") version "2.2.21"
    id("org.springframework.boot") version "4.0.6"
    id("io.spring.dependency-management") version "1.1.7"
    id("org.sonarqube") version "7.4.0.8496"
    id("org.jlleitschuh.gradle.ktlint") version "14.2.0"
    jacoco
}

group = "br.com.vercel"
version = "0.0.1-SNAPSHOT"

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(17)
    }
}

repositories {
    mavenCentral()
    google()
    mavenLocal()
}

dependencies {
    implementation("org.springframework.boot:spring-boot-starter-webmvc")
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("org.jetbrains.kotlin:kotlin-reflect")
    implementation("tools.jackson.module:jackson-module-kotlin")

    implementation("org.firebirdsql.jdbc:jaybird:2.2.15")
    implementation("org.hibernate.orm:hibernate-community-dialects")

    testImplementation("org.springframework.boot:spring-boot-starter-webmvc-test")
    testImplementation("org.jetbrains.kotlin:kotlin-test-junit5")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")

    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("org.testcontainers:junit-jupiter:1.21.4")
    testImplementation("org.testcontainers:jdbc:1.21.4")
    testImplementation("org.firebirdsql.jdbc:jaybird:6.0.6")
    testImplementation("org.testcontainers:testcontainers:2.0.5")
    testImplementation("org.firebirdsql:firebird-testcontainers-java:2.0.0")

    testImplementation("org.flywaydb:flyway-core:13.4.0")
    testImplementation("org.flywaydb:flyway-firebird:13.4.0")
    testImplementation("org.mockito.kotlin:mockito-kotlin:6.3.0")
}

kotlin {
    compilerOptions {
        freeCompilerArgs.addAll("-Xjsr305=strict", "-Xannotation-default-target=param-property")
    }
}

tasks.withType<Test> {
    useJUnitPlatform()
}

sonar {
    properties {
        property("sonar.projectKey", "emerion-service")
        property("sonar.projectName", "Emerion Service")
        property("sonar.host.url", "http://localhost:9000")
        property("sonar.token", "sqa_9eaa30e260643ad5a6d963c501bc9cfcf5bd031f")
    }
}

tasks.jacocoTestReport {
    dependsOn(tasks.test)

    reports {
        xml.required.set(true)
        html.required.set(true)
        csv.required.set(false)
    }
}

tasks.named("sonar") {
    dependsOn(tasks.jacocoTestReport)
}
