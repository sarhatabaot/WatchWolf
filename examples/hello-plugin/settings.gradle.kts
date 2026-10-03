rootProject.name = "watchwolf-example-plugin"

dependencyResolutionManagement {
    repositories {
        mavenCentral()
        maven("https://hub.spigotmc.org/nexus/content/repositories/snapshots/")
        ivy {
            url = uri("https://github.com/sarhatabaot/WatchWolf-Tester/releases/download/")
            patternLayout {
                artifact("v[revision]/[artifact]-[revision].[ext]")
            }
            metadataSources {
                artifact()
            }
            content {
                includeModule("dev.watchwolf", "watchwolf-tester")
            }
        }
    }
}
