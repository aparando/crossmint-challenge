plugins {
    kotlin("jvm") version "1.9.0"
    application
}

group = "com.crossmint.megaverse"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    // HTTP client for API calls
    implementation("com.squareup.okhttp3:okhttp:4.11.0")
    
    // JSON parsing
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin:2.15.2")
    
    // Kotlin coroutines
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.7.3")
    
    // Logging
    implementation("org.slf4j:slf4j-simple:2.0.7")
    
    // Testing
    testImplementation(kotlin("test"))
    testImplementation("org.junit.jupiter:junit-jupiter:5.9.2")
    testImplementation("io.mockk:mockk:1.13.5")
}

tasks.test {
    useJUnitPlatform()
}

kotlin {
    jvmToolchain(17)
}

application {
    mainClass.set("com.crossmint.megaverse.MegaverseApplicationKt")
}

tasks.jar {
    manifest {
        attributes["Main-Class"] = "com.crossmint.megaverse.MegaverseApplicationKt"
    }
    
    // Create a fat JAR with all dependencies
    from(configurations.runtimeClasspath.map { config ->
        config.map { if (it.isDirectory) it else zipTree(it) }
    })
    
    duplicatesStrategy = DuplicatesStrategy.EXCLUDE
}
