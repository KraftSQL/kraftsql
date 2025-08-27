plugins {
    id(libs.plugins.kotlin.jvm.get().pluginId)
    application
}

dependencies {
    implementation(project(":kraftsql"))
    implementation(libs.h2.database)

    testImplementation(libs.junit5.api)
    testImplementation(project(":kraftsql-testing"))
    testImplementation(libs.kotlin.test.junit5)
    testImplementation(libs.kotest.assertions.core)
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
}
