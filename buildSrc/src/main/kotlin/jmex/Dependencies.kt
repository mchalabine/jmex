package jmex

object Dependencies {

    object Plugin {
        const val KOTLIN = "org.jetbrains.kotlin:kotlin-gradle-plugin:${Versions.Plugin.KOTLIN}"
    }

    object Kotlin {
        const val KOTLIN = "org.jetbrains.kotlin:kotlin-stdlib-jdk8:${Versions.Plugin.KOTLIN}"
    }

    object Test {
        const val JUNIT_API = "org.junit.jupiter:junit-jupiter-api:${Versions.Test.JUNIT}"
        const val JUNIT_ENGINE = "org.junit.jupiter:junit-jupiter-engine:${Versions.Test.JUNIT}"
        const val JUNIT_SUPPORT = "org.junit.jupiter:junit-jupiter-migrationsupport:${Versions.Test.JUNIT}"
        const val JUNIT_RUNNER = "org.junit.jupiter:junit-platform-runner:${Versions.Test.JUNIT}"
        const val JUNIT_PARAMS = "org.junit.jupiter:junit-jupiter-params:${Versions.Test.JUNIT}"
    }

    object Mock {
        const val MOCK_K = "io.mockk:mockk:${Versions.Mock.MOCK_K}"
    }

    object Spring {
        const val DEPENDENCY_MANAGEMENT =
                "io.spring.gradle:dependency-management-plugin:${Versions.Spring.DEPENDENCY_MANAGEMENT}"
    }
}

