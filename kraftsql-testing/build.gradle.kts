project.description = "KrafSQL Core Testing Support"

plugins {
    id(libs.plugins.kotlin.jvm.get().pluginId)
    `java-library`
    id("test-jvm-agents")
    alias(libs.plugins.dokka.javadoc)
    alias(libs.plugins.kover)
    id("kraftsql-publishing")
}

dependencies {
    implementation(libs.junit.api)
    implementation(libs.kotest.assertions.core)
    implementation(project(":kraftsql"))

    testImplementation(libs.kotlin.test.junit5)
    testImplementation(libs.junit.params)
    testImplementation(libs.kotest.assertions.core)
    testImplementation(libs.mockito)
    testImplementation(libs.mockk)
    testAgent(libs.mockito.core) { isTransitive = false }
    testRuntimeOnly(libs.junit.engine)
    testRuntimeOnly(libs.junit.platform.launcher)
}

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(25)
    }
}
kotlin {
    compilerOptions {
        freeCompilerArgs.add("-Xcontext-parameters")
    }
}

tasks.test {
    useJUnitPlatform()
}
