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

        val executionFile = projectDir.resolve("execution/execute-shadow-jar-developq.sh")
        executionFile.writeText("java --enable-native-access=ALL-UNNAMED -jar aswe-chat-app-$projectVersion-all.jar")

        val developmentFile = projectDir.resolve("execution/execute-shadow-jar-develop.bat")
        developmentFile.writeText("java --enable-native-access=ALL-UNNAMED -jar build/libs/aswe-chat-app-$projectVersion-all.jar")

        val developmentFixedFile = projectDir.resolve("execution/execute-shadow-jar-develop-fixed.bat")
        developmentFixedFile.writeText("%userprofile%/.jdks/$jdkName/bin/java --enable-native-access=ALL-UNNAMED -jar build/libs/aswe-chat-app-$projectVersion-all.jar")

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
