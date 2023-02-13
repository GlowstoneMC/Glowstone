import org.gradle.api.tasks.testing.logging.TestExceptionFormat
import org.gradle.api.tasks.testing.logging.TestLogEvent
import java.io.ByteArrayOutputStream

plugins {
    java
    `maven-publish`
    checkstyle
    jacoco
    kotlin("jvm") version "1.7.0"
    application

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

    runtimeOnly(libs.log4j)
    runtimeOnly("com.lmax:disruptor:3.4.4")

    testImplementation(libs.bundles.junit)
    testImplementation(kotlin("test"))
    testRuntimeOnly(libs.bundles.junitRuntime)

    testImplementation(libs.hamcrest)
    testImplementation(libs.bundles.powermock)

    compileOnly(libs.jetbrains.annotations)
}


application {
    mainClass.set("net.glowstone.GlowServer")
}



group = "net.glowstone"
version = "2022.6.1-SNAPSHOT"
description = "A fast, customizable and compatible open source Minecraft server."

publishing {
    repositories {
        val mavenPublishUrl = "https://repo.glowstone.net/content/repositories/"
        val isSnapshot = version.toString().endsWith("-SNAPSHOT")
        maven {
            name = "glowstone"
            url = uri(mavenPublishUrl + if (isSnapshot) "snapshots/" else "releases/")
            credentials(PasswordCredentials::class)
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
    options.isWarnings = false;
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

tasks.withType<Test> {
    testLogging {
        showStackTraces = true
        exceptionFormat = TestExceptionFormat.FULL
        events(TestLogEvent.STANDARD_OUT)
    }
}

fun getGitHash(): String {
    val stdout = ByteArrayOutputStream()
    exec {
        commandLine = listOf("git", "rev-parse", "--short=7", "HEAD")
        standardOutput = stdout
    }
    return stdout.toString().trim()
}

fun getGitBranch(): String {
    val stdout = ByteArrayOutputStream()
    exec {
        commandLine = listOf("git", "rev-parse", "--abbrev-ref", "HEAD")
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
        val gitBranch = getGitBranch()
        val date = getGitDate(gitHash)
        attributes(
                "Launcher-Agent-Class" to "net.glowstone.util.ClassPathAgent",
                "Main-Class" to "net.glowstone.GlowServer",

                "Implementation-Title" to "Glowstone",
                "Implementation-Version" to "git-${project.name}-${gitHash}-${project.version}",
                "Implementation-Vendor" to date,

                "Specification-Title" to "Bukkit",
                "Specification-Version" to libs.versions.api.get(),
                "Specification-Vendor" to "Bukkit Team",

                "Git-Branch" to gitBranch,
                "Git-Commit" to gitHash,
        )
        for (tld in setOf("net", "com", "org")) {
            attributes("$tld/bukkit", "Sealed" to true)
        }
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

tasks.register("printMinecraftVersion") {
    doLast {
        println(providers.gradleProperty("mcVersion").get().trim())
    }
}

tasks.jacocoTestReport {
    dependsOn(tasks.test)
}
