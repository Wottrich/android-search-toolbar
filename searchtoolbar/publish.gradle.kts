import tools.handleProjectDependencies
import java.io.ByteArrayOutputStream

apply(plugin = "maven-publish")

fun hasActionUserId() = !System.getenv(EnvKeys.actionUserID).isNullOrEmpty()
fun hasActionUserPat() = !System.getenv(EnvKeys.actionUserPAT).isNullOrEmpty()
val hasPublishCredentials = true//hasActionUserId() && hasActionUserPat()

val versionName = if (hasPublishCredentials) {
    val byteOutput = ByteArrayOutputStream()
    exec {
        commandLine("git describe --tags --abbrev=0")
        standardOutput = byteOutput
    }
    byteOutput.toString().trim()
} else {
    "1.0.0-local"
}

println(versionName)

val libraryVersionName = null//versionName.orEmpty()

configure<PublishingExtension> {
    publications {
        create<MavenPublication>("searchtoolbar") {
            artifactId = Library.artifactId
            groupId = Library.groupId
            version = libraryVersionName

            artifact("$buildDir/outputs/aar/$artifactId-release.aar") {
                builtBy(tasks.getByPath("assemble"))
            }

            buildPom()
        }
        repositories {
            handleRemoteRepository()
            handleLocalRepository()
        }
    }
}

fun MavenPublication.buildPom() {
    pom.withXml {
        handleProjectDependencies(xmlProvider = this)
    }
}

fun RepositoryHandler.handleRemoteRepository() {
    maven {
        name = RepositoryKeys.remoteRepoName
        url = uri(RepositoryKeys.remoteUrl)
        credentials {
            username = System.getenv(EnvKeys.actionUserID)
            password = System.getenv(EnvKeys.actionUserPAT)
        }
    }
}

fun RepositoryHandler.handleLocalRepository() {
    mavenLocal()
}