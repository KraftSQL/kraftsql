the<SourceSetContainer>().create("integrationTest") {
    (this.extensions.getByName("kotlin") as SourceDirectorySet).srcDir("src/integrationTest/kotlin")
}.also {
    tasks.register<Test>("integrationTest") {
        description = "Runs the integration tests."
        group = "verification"
        testClassesDirs = it.output.classesDirs
        classpath = it.runtimeClasspath
        useJUnitPlatform()
    }.also { integrationTestTask -> tasks["check"].dependsOn(integrationTestTask) }
}

dependencies {
    "integrationTestRuntimeOnly"(versionCatalogs.named("libs").findLibrary("junit-engine").get())
}
