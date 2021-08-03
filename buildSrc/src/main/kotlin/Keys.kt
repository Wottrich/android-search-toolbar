import java.io.File

object EnvKeys {
    const val actionUserID = "ACTION_USER_ID"
    const val actionUserPAT = "ACTION_PERSONAL_ACCESS_TOKEN"
}

object PropertyKeys {
    const val version = "version"
}

object RepositoryKeys {
    const val remoteRepoName = "GithubPackages"
    //TODO add remote url
    const val remoteUrl = "https://maven.pkg.github.com/wottrich/android-search-toolbar"
    const val localRepoName = "LocalMavenRepo"
    fun localUrl(buildDir: File) = "file://${buildDir}/local"
}