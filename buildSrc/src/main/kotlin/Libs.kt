object Libs {

    const val kotlinStdlib = "org.jetbrains.kotlin:kotlin-stdlib:${Versions.kotlinVersion}"
    const val coreKtx = "androidx.core:core-ktx:1.3.2"
    const val appCompat = "androidx.appcompat:appcompat:1.2.0"
}

object Classpath {
    const val gradlePlugin =
        "com.android.tools.build:gradle:${Versions.Classpath.gradleVersion}"
    const val kotlinPlugin =
        "org.jetbrains.kotlin:kotlin-gradle-plugin:${Versions.kotlinVersion}"
}