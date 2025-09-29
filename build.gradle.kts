plugins {
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.detekt)
    alias(libs.plugins.kotlin.spring)
    alias(libs.plugins.spring.boot)
    alias(libs.plugins.spring.boot.dependency.management)
}

repositories {
    mavenLocal()
    mavenCentral()
}

dependencies {
    detektPlugins(libs.detekt.ktlint.formatter)

    // Spring AI
    implementation(platform(libs.spring.ai.bom))
    implementation(libs.spring.ai.starter.model.openai)
    implementation(libs.spring.ai.rag)
    implementation(libs.spring.ai.starter.vector.store.pgvector)

    // UI
    implementation(libs.kotlinx.html.jvm)

    // Data
    implementation(libs.bundles.spring.data.postgre.jpa)
    implementation(libs.liquibase)
    implementation(libs.spring.ai.spring.boot.docker.compose)

    // Kotlin
    implementation(libs.kotlin.stdlib.jdk8)
    implementation(libs.jackson.module.kotlin)

    // Kotlin Coroutines & Reactor
    implementation(libs.bundles.kotlin.coroutines.reactor)

    // Spring WebFlux
    implementation(libs.bundles.spring.webflux.core)
}

kotlin {
    jvmToolchain(21)
    compilerOptions {
        freeCompilerArgs.addAll("-Xjsr305=strict")
    }
}

tasks {
    test {
        useJUnitPlatform()
    }
}

detekt {
    config.setFrom(file("detekt.yml"))
    buildUponDefaultConfig = true
    autoCorrect = true
}

dependencyManagement {
    configurations.matching { it.name == "detekt" }.all {
        resolutionStrategy.eachDependency {
            if (requested.group == "org.jetbrains.kotlin") {
                useVersion(io.gitlab.arturbosch.detekt.getSupportedKotlinVersion())
            }
        }
    }
}
