plugins {
    alias(libs.plugins.kotlin.jvm)
    application
}

dependencies {
    implementation(project(":kraftsql"))
    implementation(libs.h2.database)
}

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(21)
    }
}
