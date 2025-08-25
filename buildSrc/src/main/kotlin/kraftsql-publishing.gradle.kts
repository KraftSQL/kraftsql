import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse

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

tasks.register<Zip>("zipStagedMavenPublication") {
    description = "Zips the staged Maven publication"
    dependsOn(tasks["publishMavenPublicationToStagingRepository"])

    from(layout.buildDirectory.dir("staging-deployment"))
    destinationDirectory = layout.buildDirectory.dir("dist")
    archiveFileName.set("staged-maven-publication-${project.version}.zip")
}

tasks.register("uploadToMavenCentral") {
    group = "publishing"
    description = "Uploads a release to Maven Central"
    dependsOn(tasks["zipStagedMavenPublication"])

    val deployment = "${project.group}:${project.name}:${project.version}"
    val uploadBundle = tasks["zipStagedMavenPublication"].outputs.files.singleFile
    doLast {
        val uploadRequest = HttpRequest.newBuilder()
            .uri(uri("https://central.sonatype.com/api/v1/publisher/upload?name=$deployment&publishingType=AUTOMATIC"))
            .header("Authorization", "BEARER ${System.getenv("MAVEN_CENTRAL_PUBLISHER_TOKEN")}")
            .POST(HttpRequest.BodyPublishers.ofFile(uploadBundle.toPath()))
            .build()
        val response = HttpClient.newHttpClient().send(uploadRequest, HttpResponse.BodyHandlers.ofString())
        if (response.statusCode() == 201) {
            logger.info("Upload to Maven Central succeeded, deployment id is '${response.body()}'")
        } else {
            logger.error("Upload to Maven Central failed: ${response.statusCode()}\n${response.body()}")
            throw IllegalStateException("Upload to Maven Central failed: ${response.statusCode()}")
        }
    }
}
