plugins {
    alias(libs.plugins.kotlin.jvm)
    `java-library`
    `maven-publish`
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

publishing {
    publications {
        create<MavenPublication>("maven") {
            from(components["java"])
        }
    }
}
