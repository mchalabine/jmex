package jmex

import org.gradle.api.initialization.Settings
import org.gradle.kotlin.dsl.SettingsScriptApi
import java.nio.file.Files
import java.nio.file.Path
import java.util.stream.Stream

object BuildSupport {

    fun includeAllSubProjects(settings: SettingsScriptApi) {
        settings.fileTree(".").matching {
            include("**/*build.gradle")
            include("**/*build.gradle.kts")
            exclude("build.gradle")
            exclude("build.gradle.kts")
            exclude("build/**/*")
            exclude("buildSrc/**/*")
            exclude("**/*/test/projects/**/*")
        }.forEach {
            val path = settings.relativePath(it.parent)
            settings.include(path)
        }
        settings.includeBuild("buildSrc")
    }

    fun setProperSubProjectNames(settings: Settings) {
        settings.rootProject.children.stream().forEach {
            it.name = settings.rootProject.name + "-" + it.name.replace('/', '-')
        }
    }

    fun getDirectories(path: Path): Stream<Path> =
            if (isValid(path)) streamDirectories(path) else streamEmpty()

    private fun streamEmpty(): Stream<Path> = Stream.empty()

    private fun streamDirectories(path: Path): Stream<Path> {
        return Files.list(path).filter(Files::isDirectory)
    }

    private fun isValid(path: Path) = Files.isDirectory(path)

}
