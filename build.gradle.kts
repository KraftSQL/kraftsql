allprojects {
    group = "rocks.frieler.kraftsql"
    version = "0.0.3-SNAPSHOT"

    repositories {
        mavenCentral()
    }
}

tasks.register("versionFile") {
    group = "build"
    description = "Writes the current package version to 'build/version.txt'."

    val versionTxt = layout.buildDirectory.get().file("version.txt").asFile
    val version = project.version.toString()
    doLast {
        versionTxt.apply {
            parentFile.mkdirs()
            writeText(version)
        }
    }
}
