plugins {
    alias(libs.plugins.kotlin.jvm)
    application
}

dependencies {
    implementation(project(":kraftsql"))
    implementation(project(":kraftsql-testing"))
    implementation(libs.h2.database)
}

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(21)
    }
}
