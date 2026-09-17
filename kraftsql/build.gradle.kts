project.description = "KrafSQL Core Library"

plugins {
    id(libs.plugins.kotlin.jvm.get().pluginId)
    `java-library`
    idea
}

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(25)
    }
    tasks.test {
        useJUnitPlatform()
    }

    registerFeature("referenceTest") {
        usingSourceSet(sourceSets.register("referenceTest") {
            kotlin.srcDir("src/referenceTest/kotlin")
            idea.module.testSources.from(kotlin.srcDirs)
        }.get())
    }
}

dependencies {
    api(libs.kotlin.reflect)

    testImplementation(libs.junit.api)
    testImplementation(libs.kotest.assertions.core)
    testImplementation(libs.mockk)
    testRuntimeOnly(libs.junit.engine)
    testRuntimeOnly(libs.junit.platform.launcher)

    "referenceTestImplementation"(project(":kraftsql"))
    "referenceTestImplementation"(libs.junit.api)
    "referenceTestImplementation"(libs.kotest.assertions.core)
}
