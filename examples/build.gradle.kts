plugins {
    id(libs.plugins.kotlin.jvm.get().pluginId)
    application
}

dependencies {
    implementation(project(":kraftsql"))
    implementation(libs.h2.database)

    testImplementation(project(":kraftsql-testing"))
    testImplementation(libs.kotlin.test.junit5)
    testImplementation(libs.kotest.assertions.core)
    testImplementation(libs.junit.api)
    testRuntimeOnly(libs.junit.engine)
    testRuntimeOnly(libs.junit.platform.launcher)
}

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(25)
    }
}

tasks.test {
    useJUnitPlatform()
}
