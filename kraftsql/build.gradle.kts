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
    testImplementation(libs.h2.database)
    testRuntimeOnly(libs.junit.engine)
    testRuntimeOnly(libs.junit.platform.launcher)
}

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(24)
    }
}
kotlin {
    compilerOptions {
        freeCompilerArgs.add("-Xreturn-value-checker=disable")
    }
}

tasks.test {
    useJUnitPlatform()
}
