plugins {
    alias(libs.plugins.kotlin.jvm)
    application
}

dependencies {
    implementation(project(":kraftsql"))
    implementation(project(":kraftsql-testing"))
    implementation(libs.h2.database)

    testImplementation(libs.junit5.api)
    testImplementation(project(":kraftsql-testing"))
}

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(21)
    }
}

tasks.test {
    useJUnitPlatform()
}
