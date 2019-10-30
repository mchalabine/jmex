package jmex

import org.gradle.api.JavaVersion

object Versions {

    object Java {
        val TARGET_JVM = JavaVersion.VERSION_12.toString()
    }

    object Gradle {
        const val WRAPPER = "5.5.1"
    }

    object Plugin {
        const val KOTLIN = "1.3.50"
    }

    object Test {
        const val JUNIT = "5.5.2"
    }

    object Mock {
        const val MOCK_K = "1.9.3"
    }

    object Spring {
    }

}

