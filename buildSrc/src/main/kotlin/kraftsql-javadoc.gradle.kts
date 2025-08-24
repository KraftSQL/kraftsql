plugins {
    id("org.jetbrains.dokka-javadoc")
}

dokka {
    project.extensions.findByType<JavaPluginExtension>()?.also { javaPlugin ->
        val mainSourceSet = javaPlugin.sourceSets["main"]
        dokkaSourceSets.register(mainSourceSet.name) {
            sourceRoots.from(mainSourceSet.allJava.srcDirs)
        }
    }
}

tasks.register<Jar>("javadocJar") {
    group = "documentation"
    description = "Assembles a jar archive containing the javadoc documentation."
    dependsOn(tasks.dokkaGeneratePublicationJavadoc)
    from(tasks.dokkaGeneratePublicationJavadoc.get().outputDirectory)
    archiveClassifier.set("javadoc")
}
