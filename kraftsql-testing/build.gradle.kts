plugins {
    id(libs.plugins.kotlin.jvm.get().pluginId)
    `java-library`
    alias(libs.plugins.dokka.javadoc)
    id("kraftsql-publishing")
}

dependencies {
    implementation(libs.junit5.api)
    implementation(project(":kraftsql"))
    implementation(libs.kotest.assertions.api)
    implementation(libs.kotest.assertions.shared)
}

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(21)
    }
}
