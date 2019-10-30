import org.jetbrains.kotlin.gradle.plugin.getKotlinPluginVersion
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    id(jmex.PluginIds.JetBrains.KOTLIN).version(jmex.Versions.Plugin.KOTLIN)
    id(jmex.PluginIds.Maven.MAVEN_PUBLISH)
}

val kotlinVersion = getKotlinPluginVersion()

allprojects {
    repositories {
        google()
        gradlePluginPortal()
        mavenLocal()
    }

    tasks {
        withType<Wrapper> {
            gradleVersion = jmex.Versions.Gradle.WRAPPER
            distributionType = Wrapper.DistributionType.ALL
        }

        withType<KotlinCompile> {
            kotlinOptions.jvmTarget = jmex.Versions.Java.TARGET_JVM
        }

        withType<Test> {
            useJUnitPlatform {
                includeEngines("junit-jupiter", "junit-vintage")
            }
        }

        withType<JavaCompile> {
            sourceCompatibility = jmex.Versions.Java.TARGET_JVM
            targetCompatibility = jmex.Versions.Java.TARGET_JVM
        }
    }
}

dependencies {
    implementation(kotlin("stdlib", kotlinVersion))
    implementation(kotlin("reflect", kotlinVersion))
    compile(kotlin("gradle-plugin", kotlinVersion))
    compile(kotlin("allopen", kotlinVersion))

    subprojects.forEach {
        archives(it)
    }
}


