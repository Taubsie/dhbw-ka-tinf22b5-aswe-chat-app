plugins {
    id("java")
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

tasks.test {
    useJUnitPlatform()
}
