
object BuildPlugins {
    const val androidApplication = "com.android.application"
    const val androidLibrary = "com.android.library"
    const val kotlinAndroid = "kotlin-android"
}

object ArchivePath {
    const val publishLibrary = "publish.gradle.kts"
}

object AndroidSdk {
    const val min = 21
    const val compile = 30
    const val target = compile
    const val jvmTarget = "1.8"
    const val buildTools = "30.0.3"
}