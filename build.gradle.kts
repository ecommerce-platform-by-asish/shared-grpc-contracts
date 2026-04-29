plugins {
    id("java-library")
    id("maven-publish")
    alias(libs.plugins.springboot)
    alias(libs.plugins.spotless)
    alias(libs.plugins.protobuf)
}

group = "com.app"
version = "1.0.0-SNAPSHOT"

java {
    toolchain { languageVersion.set(JavaLanguageVersion.of(25)) }
    withSourcesJar()
}

dependencies {
    api(platform(libs.sb.bom))
    api(platform(libs.spring.grpc.bom))
    api(libs.protobuf.java)
    api(libs.spring.grpc.starter)
    implementation("javax.annotation:javax.annotation-api:1.3.2")
}

protobuf {
    protoc {
        artifact = "com.google.protobuf:protoc:${libs.versions.protobuf.get()}"
    }
    plugins {
        create("grpc") {
            artifact = "io.grpc:protoc-gen-grpc-java:1.70.0" 
        }
    }
    generateProtoTasks {
        all().forEach {
            it.plugins {
                create("grpc")
            }
        }
    }
}

publishing {
    publications {
        create<MavenPublication>("maven") {
            from(components["java"])
        }
    }
}

tasks.withType<JavaCompile>().configureEach {
    options.encoding = "UTF-8"
    options.compilerArgs.removeAll { it == "--enable-preview" }
}

spotless {
    java {
        targetExclude("build/generated/**/*.java")
        googleJavaFormat("1.27.0")
        removeUnusedImports()
    }
}

tasks.bootJar { enabled = false }
tasks.jar { enabled = true }

tasks.build {
    finalizedBy("publishToMavenLocal")
}

tasks.processResources {
    duplicatesStrategy = DuplicatesStrategy.INCLUDE
}

tasks.clean {
    mustRunAfter("spotlessApply")
}

// Ensure proto files are included in the JAR
sourceSets {
    main {
        resources {
            srcDir("src/main/proto")
        }
    }
}
