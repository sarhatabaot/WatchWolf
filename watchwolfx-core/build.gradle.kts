import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar

plugins {
    `java-library`
    `maven-publish`
    id("com.gradleup.shadow") version "9.4.3"
}

group = "dev.watchwolfx"
version = "0.5.2"

base {
    archivesName.set("watchwolfx-core")
}

sourceSets {
    main {
        java.srcDir("src/scripts/java")
    }
}

dependencies {
    api("org.reflections:reflections:0.10.2")
    api("org.apache.logging.log4j:log4j-api:2.23.1")
    api("org.apache.logging.log4j:log4j-core:2.23.1")
    api("com.google.code.gson:gson:2.10.1")
    api("de.dev-eth0:jCodeGen:0.11-2")

    testImplementation("org.junit.jupiter:junit-jupiter-engine:5.8.1")
    testImplementation("org.junit.jupiter:junit-jupiter-params:5.8.1")
    testImplementation("org.junit.platform:junit-platform-launcher:1.8.1")
    testImplementation("org.mockito:mockito-core:2.7.5")
    testImplementation("com.google.jimfs:jimfs:1.3.0")
}

val integrationTestSourceSet = sourceSets.create("integrationTest") {
    java.srcDir("src/integration-test/java")
    compileClasspath += sourceSets.main.get().output + sourceSets.test.get().output + configurations.testRuntimeClasspath.get()
    runtimeClasspath += output + compileClasspath
}

configurations[integrationTestSourceSet.implementationConfigurationName]
    .extendsFrom(configurations.testImplementation.get())
configurations[integrationTestSourceSet.runtimeOnlyConfigurationName]
    .extendsFrom(configurations.testRuntimeOnly.get())

tasks.withType<JavaCompile>().configureEach {
    options.release.set(8)
    options.encoding = "UTF-8"
}

tasks.test {
    useJUnitPlatform()
    include("**/*Should.class")
    exclude("**/IT*.class")
}

val integrationTest = tasks.register<Test>("integrationTest") {
    description = "Runs the socket-backed integration tests."
    group = LifecycleBasePlugin.VERIFICATION_GROUP
    testClassesDirs = integrationTestSourceSet.output.classesDirs
    classpath = integrationTestSourceSet.runtimeClasspath
    useJUnitPlatform()
    include("**/IT*.class")
    shouldRunAfter(tasks.test)
}

tasks.register<JavaExec>("generateRpcStubs") {
    description = "Regenerates RPC stubs from the pinned API definitions."
    group = "code generation"
    classpath = sourceSets.main.get().runtimeClasspath
    mainClass.set("dev.watchwolf.rpc.RPCComponentsCreator")
    dependsOn(tasks.classes)
}

tasks.named<ShadowJar>("shadowJar") {
    archiveClassifier.set("")
}

tasks.jar {
    enabled = false
}

tasks.assemble {
    dependsOn(tasks.shadowJar)
}

val githubRepository = providers.environmentVariable("GITHUB_REPOSITORY")
    .orElse("sarhatabaot/WatchWolfX")

publishing {
    publications {
        create<MavenPublication>("core") {
            artifact(tasks.shadowJar)
            artifactId = "watchwolfx-core"
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
