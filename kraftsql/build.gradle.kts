plugins {
    alias(libs.plugins.kotlin.jvm)
    `java-library`
}

dependencies {
}

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(21)
    }
}
