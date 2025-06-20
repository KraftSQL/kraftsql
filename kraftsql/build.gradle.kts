plugins {
    alias(libs.plugins.kotlin.jvm)
    `java-library`
}

dependencies {
    api(libs.kotlin.reflect)
}

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(21)
    }
}
