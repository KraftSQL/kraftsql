plugins {
    alias(libs.plugins.kotlin.jvm)
    `java-library`
}

dependencies {
    implementation(project(":kraftsql"))
}

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(21)
    }
}
