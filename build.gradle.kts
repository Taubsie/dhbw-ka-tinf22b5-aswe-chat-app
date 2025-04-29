import org.gradle.internal.jvm.Jvm
import java.util.*

plugins {
    id("java")
    id("application")
    kotlin("jvm")
    id("com.gradleup.shadow") version "8.3.5"
}

group = "de.dhbw.ka.tinf22b5"
version = "1.0.0"

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.jetbrains:annotations:26.0.2")

    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
    testImplementation(kotlin("test"))
}

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(23)
    }
}

application {
    mainClass = "de.dhbw.ka.tinf22b5.Main"
}

tasks.test {
    useJUnitPlatform()
}

task("createProperties") {
    dependsOn("processResources")
    resources.text

    doLast {
        val projectVersion = project.version.toString()
        val jdkName = Jvm.current().javaHome.name ?: "openjdk-23.0.1"
        val jarFile = projectDir.resolve("build/libs/aswe-chat-app-$projectVersion-all.jar").absolutePath

        val executionFile = projectDir.resolve("execution/execute-shadow-jar-develop.sh")
        executionFile.parentFile.mkdirs()
        executionFile.writeText("java --enable-native-access=ALL-UNNAMED -jar $jarFile")

        val developmentFile = projectDir.resolve("execution/execute-shadow-jar-develop.bat")
        developmentFile.parentFile.mkdirs()
        developmentFile.writeText("java --enable-native-access=ALL-UNNAMED -jar $jarFile")

        val developmentFixedFile = projectDir.resolve("execution/execute-shadow-jar-develop-fixed.bat")
        developmentFixedFile.parentFile.mkdirs()
        developmentFixedFile.writeText("%userprofile%/.jdks/$jdkName/bin/java --enable-native-access=ALL-UNNAMED -jar $jarFile")

        val p = Properties()
        p["version"] = projectVersion

        val versionFile = File("${layout.buildDirectory.asFile.get().absolutePath}/resources/main/config/version.properties")
        versionFile.parentFile.mkdirs()

        p.store(versionFile.writer(), null)
    }
}

tasks.classes {
    dependsOn("createProperties")
}
