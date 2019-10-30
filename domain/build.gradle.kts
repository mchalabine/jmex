description = "jMeX Core Domain"

plugins {
    id(jmex.PluginIds.Java.GRADLE)
    id(jmex.PluginIds.JetBrains.KOTLIN)
}

dependencies {
    implementation()
    testImplementation()
}

fun DependencyHandlerScope.implementation() {
    implementationKotlin()
    implementationBuildSrc()
}

fun DependencyHandlerScope.implementationKotlin() {
    implementation(jmex.Dependencies.Kotlin.KOTLIN)
}

fun DependencyHandlerScope.implementationBuildSrc() {
    implementation(fileTree("${project.rootDir}/buildSrc/build/"))
}

fun DependencyHandlerScope.testImplementation() {
    testImplementationTest()
    testImplementationMock()
    testImplementationBuildSrc()
}

fun DependencyHandlerScope.testImplementationTest() {
    testImplementation(jmex.Dependencies.Test.JUNIT_API)
    testRuntimeOnly(jmex.Dependencies.Test.JUNIT_ENGINE)
}

fun DependencyHandlerScope.testImplementationMock() {
    testImplementation(jmex.Dependencies.Mock.MOCK_K)
}

fun DependencyHandlerScope.testImplementationBuildSrc() {
    testImplementation(fileTree("${project.rootDir}/buildSrc/build/"))
}
