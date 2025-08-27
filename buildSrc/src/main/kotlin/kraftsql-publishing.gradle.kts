plugins {
    id("com.vanniktech.maven.publish")
}

publishing {
    repositories {
        maven {
            name = "GitHubPackages"
            url = uri("https://maven.pkg.github.com/KraftSQL/kraftsql")
            credentials {
                username = findProperty("github_packages_publishing_user")?.toString()
                password = findProperty("github_packages_publishing_password")?.toString()
            }
        }
    }
}
