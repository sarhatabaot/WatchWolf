plugins {
    java
}

group = "dev.watchwolf.example"
version = "1.0.0"

dependencies {
    compileOnly("org.spigotmc:spigot-api:1.16.5-R0.1-SNAPSHOT")

    testImplementation("dev.watchwolf:watchwolf-tester:0.4.1")
    testImplementation("org.yaml:snakeyaml:1.21")
    testImplementation("org.junit.jupiter:junit-jupiter:5.14.4")
    testImplementation("org.junit.jupiter:junit-jupiter-params:5.14.4")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

tasks.compileJava {
    options.release.set(8)
}

tasks.jar {
    archiveFileName.set("watchwolf-example-plugin.jar")
}

tasks.test {
    dependsOn(tasks.jar)
    useJUnitPlatform()
}
