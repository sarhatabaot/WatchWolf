plugins {
    `java-library`
    `maven-publish`
}

group = "dev.watchwolfx"
version = "0.5.1"

val githubRepository = providers.environmentVariable("GITHUB_REPOSITORY")
    .orElse("sarhatabaot/WatchWolfX")

base {
    archivesName.set("watchwolfx-tester")
}

dependencies {
    api("org.junit.jupiter:junit-jupiter-engine:5.8.1")
    api("org.junit.jupiter:junit-jupiter-params:5.8.1")
    api("org.yaml:snakeyaml:1.21")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher:1.8.1")
}

tasks.compileJava {
    options.release.set(8)
    options.encoding = "UTF-8"
}

tasks.test {
    useJUnitPlatform()
    enabled = providers.gradleProperty("runTests").map(String::toBoolean).getOrElse(false)
}

publishing {
    publications {
        create<MavenPublication>("tester") {
            from(components["java"])
            artifactId = "watchwolfx-tester"
        }
    }
    repositories {
        maven {
            name = "GitHubPackages"
            url = uri("https://maven.pkg.github.com/${githubRepository.get()}")
            credentials {
                username = providers.environmentVariable("GITHUB_ACTOR").orNull
                password = providers.environmentVariable("GITHUB_TOKEN").orNull
            }
        }
    }
}
