plugins {
    id("java")
    id("application")
    id("com.gradleup.shadow") version "8.3.5"
}

group = "de.dhbw.ka.tinf22b5"
version = "1.0.0"

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(platform("org.junit:junit-bom:5.10.0"))
    testImplementation("org.junit.jupiter:junit-jupiter")
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
