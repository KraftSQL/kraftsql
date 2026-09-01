plugins {
    id(libs.plugins.kotlin.jvm.get().pluginId)
    application
}

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(25)
    }
}

dependencies {
    implementation(project(":kraftsql"))
}
