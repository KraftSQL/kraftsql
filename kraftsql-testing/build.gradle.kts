plugins {
    alias(libs.plugins.kotlin.jvm)
    `java-library`
}

dependencies {
    implementation(libs.junit5.api)
    implementation(project(":kraftsql"))
}

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(21)
    }
}
