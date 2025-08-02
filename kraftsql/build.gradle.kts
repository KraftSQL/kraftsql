plugins {
    alias(libs.plugins.kotlin.jvm)
    `java-library`
    id("kraftsql-publishing")
}

dependencies {
    api(libs.kotlin.reflect)
}

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(21)
    }
}
