plugins {
    `maven-publish`
    signing
}

publishing {
    publications {
        create<MavenPublication>("maven") {
            from(components["java"])
            artifact(tasks["kotlinSourcesJar"])
            artifact(tasks["javadocJar"])
        }
    }
    repositories {
        maven {
            name = "GitHubPackages"
            url = uri("https://maven.pkg.github.com/KraftSQL/kraftsql")
            credentials {
                username = findProperty("github_packages_publishing_user")?.toString()
                password = findProperty("github_packages_publishing_password")?.toString()
            }
        }
        maven {
            name = "Staging"
            url = uri(layout.buildDirectory.dir("staging-deployment"))
        }
    }
}

signing {
    sign(publishing.publications["maven"])
    isRequired = !project.version.toString().endsWith("-SNAPSHOT")
    useInMemoryPgpKeys(
        project.properties["signingKey"]?.toString(),
        project.properties["signingPassword"]?.toString())
}
