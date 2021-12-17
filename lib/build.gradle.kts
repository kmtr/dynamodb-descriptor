plugins {
    kotlin("jvm") version "1.6.10"
    kotlin("plugin.serialization") version "1.6.10"
    `java-library`
}

repositories {
    mavenCentral()
    // for DynamoDBLocal
    maven("https://s3.ap-northeast-1.amazonaws.com/dynamodb-local-tokyo/release")
}

val sqlite4Version = "1.0.392"

dependencies {
    implementation(platform("org.jetbrains.kotlin:kotlin-bom"))
    implementation("com.charleskorn.kaml:kaml:0.38.0")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-core-jvm:1.3.1")

    testImplementation("org.jetbrains.kotlin:kotlin-test")
    testImplementation("org.jetbrains.kotlin:kotlin-test-junit")

    implementation(platform("software.amazon.awssdk:bom:2.17.97"))
    implementation("software.amazon.awssdk:dynamodb")
    implementation("software.amazon.awssdk:dynamodb-enhanced")

    // for DynamoDBLocal
    testImplementation("com.amazonaws:DynamoDBLocal:1.16.0")
    testImplementation("com.almworks.sqlite4java:sqlite4java:${sqlite4Version}")
    testImplementation("com.almworks.sqlite4java:libsqlite4java-linux-amd64:${sqlite4Version}")
    testImplementation("com.almworks.sqlite4java:sqlite4java-win32-x64:${sqlite4Version}")
    testImplementation("com.almworks.sqlite4java:libsqlite4java-osx:${sqlite4Version}")

}

// run DynamoDBLocal when `gradle test`
val buildNativeLibsPath = "build/native-libs"
val copyNativeDeps = tasks.register<Copy>("copyNativeDeps") {
    mkdir(buildNativeLibsPath)
    from(configurations.testCompileClasspath) {
        include("*.dll")
        include("*.dylib")
        include("*.so")
    }
    into(buildNativeLibsPath)
}

tasks.test {
    dependsOn(copyNativeDeps)
    doFirst {
        systemProperties(("java.library.path" to buildNativeLibsPath))
    }
}

