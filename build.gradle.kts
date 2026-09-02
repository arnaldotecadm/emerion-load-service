plugins {
    kotlin("jvm") version "2.2.21"
    kotlin("plugin.spring") version "2.2.21"
    id("org.springframework.boot") version "4.0.6"
    id("io.spring.dependency-management") version "1.1.7"
    id("org.sonarqube") version "7.4.0.8496"
    id("org.jlleitschuh.gradle.ktlint") version "14.2.0"
    id("org.openapi.generator") version "7.17.0"
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
    implementation(libs.spring.boot.starter.data.jpa)
    implementation(libs.spring.boot.starter.webmvc)
    implementation(libs.kotlin.reflect)
    implementation(libs.jackson.module.kotlin)
    implementation(libs.jaybird.legacy)
    implementation(libs.hibernate.community.dialects)

    testImplementation(libs.spring.boot.starter.webmvc.test)
    testImplementation(libs.kotlin.test.junit5)
    testImplementation(libs.spring.boot.starter.test)
    testImplementation(libs.testcontainers.junit.jupiter)
    testImplementation(libs.testcontainers.jdbc)
    testImplementation(libs.jaybird)
    testImplementation(libs.testcontainers.core)
    testImplementation(libs.firebird.testcontainers.java)
    testImplementation(libs.flyway.core)
    testImplementation(libs.flyway.firebird)
    testImplementation(libs.mockito.kotlin)

    testRuntimeOnly(libs.junit.platform.launcher)
}

kotlin {
    compilerOptions {
        freeCompilerArgs.addAll("-Xjsr305=strict", "-Xannotation-default-target=param-property")
    }
}

openApiGenerate {
    generatorName.set("kotlin")
    inputSpec.set("$projectDir/src/main/resources/openapi.yaml")
    outputDir.set("$buildDir/generated/openapi")
    modelPackage.set("br.com.vercel.emerionloadservice.api.model")
    globalProperties.set(
        mapOf(
            "apis" to "false",
            "models" to "",
            "supportingFiles" to "false",
        ),
    )
    configOptions.set(
        mapOf(
            "library" to "jvm-spring-webclient",
            "dateLibrary" to "java8-localdatetime",
            "serializationLibrary" to "jackson",
            "useBeanValidation" to "false",
        ),
    )
    typeMappings.set(
        mapOf(
            "DateTime" to "Instant",
            "date-time-local" to "LocalDateTime",
        ),
    )
    importMappings.set(
        mapOf(
            "Instant" to "java.time.Instant",
            "LocalDateTime" to "java.time.LocalDateTime",
        ),
    )
}

sourceSets {
    main {
        kotlin.srcDir("$buildDir/generated/openapi/src/main/kotlin")
    }
}

tasks.named("compileKotlin") {
    dependsOn(tasks.named("openApiGenerate"))
}

gradle.projectsEvaluated {
    tasks.named<org.jlleitschuh.gradle.ktlint.tasks.KtLintCheckTask>("runKtlintCheckOverMainSourceSet") {
        setSource("src/main/kotlin")
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
        property("sonar.token", System.getenv("SONAR_TOKEN"))
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
