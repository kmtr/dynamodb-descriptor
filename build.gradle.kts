subprojects {
    apply(plugin = "maven-publish")
    configure<PublishingExtension> {
        repositories {
            maven {
                name = "GitHubPackages"
                url = uri("https://maven.pkg.github.com/kmtr/dynamodb-descriptor")
                credentials {
                    username = project.findProperty("gpr.user") as String? ?: System.getenv("USERNAME")
                    password = project.findProperty("gpr.key") as String? ?: System.getenv("TOKEN")
                }
            }
        }
        publications {
            create<MavenPublication>("gpr") {
                groupId = "kmtr.github.com"
                artifactId = "dynamodb-descriptor"
                version = "0.1.0-SNAPSHOT"
            }
        }
    }
}
