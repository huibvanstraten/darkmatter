plugins {
    kotlin("multiplatform") version Versions.kotlin apply (false)
}

subprojects {
    version = "1.3"
    repositories {
        jcenter()
        google()
        maven("https://oss.sonatype.org/content/repositories/snapshots/")
    }
}
