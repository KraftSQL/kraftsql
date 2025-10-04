project.description = "KrafSQL Core Library"

plugins {
    id(libs.plugins.kotlin.jvm.get().pluginId)
    `java-library`
    jacoco
    alias(libs.plugins.dokka.javadoc)
    id("kraftsql-publishing")
}

dependencies {
    api(libs.kotlin.reflect)

    testImplementation(libs.kotlin.test.junit5)
    testImplementation(libs.kotest.assertions.core)
    testImplementation(libs.mockito)
    testRuntimeOnly(libs.junit5.engine)
    testRuntimeOnly(libs.junit.platform.launcher)
}

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(21)
    }
}

tasks.test {
    useJUnitPlatform()
    finalizedBy(tasks.jacocoTestReport)
}
tasks.jacocoTestReport {
    reports {
        xml.required = true
        html.required = false
    }
}
