rootProject.name = "watchwolf-example-plugin"

val watchWolfXReleaseRepository = providers
    .environmentVariable("WATCHWOLFX_RELEASE_REPOSITORY")
    .orElse("sarhatabaot/WatchWolf")
    .get()

dependencyResolutionManagement {
    repositories {
        mavenCentral()
        maven("https://hub.spigotmc.org/nexus/content/repositories/snapshots/")
        ivy {
            url = uri("https://github.com/$watchWolfXReleaseRepository/releases/download/")
            patternLayout {
                artifact("v[revision]/[artifact]-[revision].[ext]")
            }
            metadataSources {
                artifact()
            }
            content {
                includeModule("dev.watchwolfx", "watchwolfx-tester")
            }
        }
    }
}
