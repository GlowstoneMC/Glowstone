import java.io.ByteArrayOutputStream
import java.util.*

plugins {
    java
    `maven-publish`
    checkstyle
    jacoco
    kotlin("jvm") version "1.7.0"

    id("io.freefair.lombok") version "6.5.0-rc1"
    id("com.github.johnrengelman.shadow") version "7.1.2"
}

val buildExtras = System.getenv("BUILD_EXTRAS")?.toBoolean() ?: false

repositories {
    mavenLocal()

    maven("https://repo.glowstone.net/repository/maven-public/")
    maven("https://repo.glowstone.net/repository/snapshots/")
    maven("https://libraries.minecraft.net")
}

dependencies {
    implementation(libs.bundles.linkstone)
    implementation(libs.bundles.glowstone)

    implementation(libs.jansi)
    implementation(libs.jline)
    implementation(libs.fastutil)
    implementation(libs.flow)
    implementation(libs.fastuuid)
    implementation(libs.brigadier)
    implementation(libs.gluegen)
    implementation(libs.jocl)
    implementation(libs.naether) {
        exclude(group = "org.slf4j", module = "slf4j-simple")
    }
    implementation(libs.maven.artifact)

    runtimeOnly(libs.slf4j.jdk14)

    testImplementation(libs.bundles.junit)
    testImplementation(kotlin("test"))
    testRuntimeOnly(libs.bundles.junitRuntime)

    testImplementation(libs.hamcrest)
    testImplementation(libs.bundles.powermock)

    compileOnly(libs.jetbrains.annotations)
}

group = "net.glowstone"
version = "2022.6.1-SNAPSHOT"
description = "A fast, customizable and compatible open source Minecraft server."

publishing {
    repositories {
        val mavenPublishUrl = "https://repo.glowstone.net/content/repositories/"
        val isSnapshot = version.toString().endsWith("-SNAPSHOT")
        maven {
            url = uri(mavenPublishUrl + if (isSnapshot) "snapshots/" else "releases/")
            credentials {
                username = System.getenv("MAVEN_USERNAME")
                password = System.getenv("MAVEN_PASSWORD")
            }
        }
    }
    publications {
        register<MavenPublication>("maven") {
            withoutBuildIdentifier()
            from(components["java"])
            pom {
                name.set(project.name)
                description.set(project.description)
                url.set("https://www.glowstone.net")
                inceptionYear.set("2011")
                packaging = "jar"

                licenses {
                    license {
                        name.set("MIT")
                        url.set("https://github.com/GlowstoneMC/Glowstone/blob/dev/LICENSE")
                        distribution.set("repo")
                    }
                }
            }
        }
    }
}

checkstyle {
    configFile = File(project.projectDir, "/etc/checkstyle.xml")
    configProperties["checkstyle.header.file"] = File(project.projectDir, "LICENSE")
    toolVersion = libs.versions.checkstyle.get()
    isIgnoreFailures = true
}

val javaVersion = 17

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(javaVersion))
    }

    if (buildExtras) {
        withSourcesJar()
        tasks.named<Jar>("sourcesJar") {
            archiveVersion.set("")
        }

        withJavadocJar()
        tasks.named<Jar>("javadocJar") {
            archiveVersion.set("")
        }
    }
}

kotlin {
    jvmToolchain {
        (this as JavaToolchainSpec).apply {
            languageVersion.set(JavaLanguageVersion.of(javaVersion))
        }
    }
}

tasks.withType<JavaCompile> {
    options.encoding = Charsets.UTF_8.name()
    options.release.set(javaVersion)
}

tasks.withType<Javadoc> {
    options.encoding = Charsets.UTF_8.name()

    if (JavaVersion.current().isJava9Compatible) {
        (options as StandardJavadocDocletOptions).addBooleanOption("html5", true)
    }

    exclude("**/*.xml")
}

tasks.withType<ProcessResources> {
    filteringCharset = Charsets.UTF_8.name()
}

fun getGitHash(): String {
    val stdout = ByteArrayOutputStream()
    exec {
        commandLine = listOf("git", "rev-parse", "--short=7", "HEAD")
        standardOutput = stdout
    }
    return stdout.toString().trim()
}

fun getGitDate(gitHash: String): String {
    val stdout = ByteArrayOutputStream()
    exec {
        commandLine = listOf("git", "show", "-s", "--format=%ci", gitHash)
        standardOutput = stdout
    }
    return stdout.toString().trim()
}

tasks.jar {
    enabled = false
    manifest {
        val gitHash = getGitHash()
        val date = getGitDate(gitHash)
        attributes(
                "Launcher-Agent-Class" to "net.glowstone.util.ClassPathAgent",
                "Main-Class" to "net.glowstone.GlowServer",

                "Implementation-Title" to "Glowstone",
                "Implementation-Version" to "git-${project.name}-${gitHash}-${project.version}",
                "Implementation-Vendor" to date,

                "Specification-Title" to "Bukkit",
                "Specification-Version" to libs.versions.api.get(),
                "Specification-Vendor" to "Bukkit Team"
        )
    }
}

tasks.shadowJar {
    val prefix = "org.bukkit.craftbukkit.libs"
    listOf(
        "jline",
        "it.unimi",
    ).forEach { pattern ->
        relocate(pattern, "$prefix.$pattern")
    }

    exclude("META-INF/*.SF", "META-INF/*.DSA", "META-INF/*.RSA", "OSGI-INF/**", "*.profile", "module-info.class", "ant_tasks/**", "mojang-translations/*")

    archiveVersion.set("")
    archiveClassifier.set("")
}

tasks.assemble { dependsOn(tasks.shadowJar) }

tasks.getByName<Test>("test") {
    useJUnitPlatform()
    finalizedBy(tasks.jacocoTestReport)
}

tasks.jacocoTestReport {
    dependsOn(tasks.test)
}
