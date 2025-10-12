project.description = "KrafSQL Core Library"

plugins {
    id(libs.plugins.kotlin.jvm.get().pluginId)
    `java-library`
    alias(libs.plugins.dokka.javadoc)
    alias(libs.plugins.kover)
    id("kraftsql-publishing")
}

dependencies {
    api(libs.kotlin.reflect)

    testImplementation(libs.kotlin.test.junit5)
    testImplementation(libs.kotest.assertions.core)
    testImplementation(libs.mockito)
    testRuntimeOnly(libs.junit.engine)
    testRuntimeOnly(libs.junit.platform.launcher)
}

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(21)
    }
}

tasks.test {
    useJUnitPlatform()
}
