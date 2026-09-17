plugins {
    id(libs.plugins.kotlin.jvm.get().pluginId)
    application
    id("integration-testing")
}

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(25)
    }
    tasks.test {
        useJUnitPlatform()
    }
}

dependencies {
    implementation(project(":kraftsql"))
    runtimeOnly(libs.h2.database)

    testImplementation(libs.junit.api)
    testImplementation(libs.kotest.assertions.core)
    testImplementation(libs.mockk)
    testRuntimeOnly(libs.junit.engine)
    testRuntimeOnly(libs.junit.platform.launcher)

    "integrationTestImplementation"(libs.junit.platform.suite)
    "integrationTestRuntimeOnly"(project(":examples"))
    "integrationTestRuntimeOnly"(project(":kraftsql")) {
        capabilities { requireCapability("${group}:kraftsql-reference-test:${version}") }
    }
}
