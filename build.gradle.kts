import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar
import net.minecrell.pluginyml.bukkit.BukkitPluginDescription
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    java
    kotlin("jvm") version "1.8.10"
    id("com.github.johnrengelman.shadow") version "7.1.2"
    id("io.papermc.paperweight.userdev") version "1.5.2"
    id("xyz.jpenilla.run-paper") version "2.0.1"
    id("net.minecrell.plugin-yml.bukkit") version "0.5.3"
}

repositories {
    mavenLocal()
    mavenCentral()
    maven("https://papermc.io/repo/repository/maven-public/")
    maven("https://hub.spigotmc.org/nexus/content/repositories/snapshots/")
    maven("https://oss.sonatype.org/content/groups/public/")
    maven("https://repo.inventivetalent.org/repository/maven-snapshots/")
    maven("https://jitpack.io/")
}


val shadowMe by configurations.creating {
    configurations.implementation.get().extendsFrom(this)
}
dependencies {
    paperweight.paperDevBundle("1.18.2-R0.1-SNAPSHOT")

/*
    implementation(platform("org.jetbrains.kotlin:kotlin-bom"))
    shadowMe("org.jetbrains.kotlin:kotlin-stdlib")
    shadowMe("org.jetbrains.kotlin:kotlin-reflect")
    shadowMe("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.6.4")
*/

    implementation("net.axay:kspigot:1.18.2")

    implementation("com.github.booksaw:BetterTeams:4.6.2")
}

group = "mynameisjeff"
version = "1.0-SNAPSHOT"
description = "sbmeventplugin"

bukkit {
    load = BukkitPluginDescription.PluginLoadOrder.STARTUP
    main = "mynameisjeff.sbmeventplugin.SBMEventPlugin"
    apiVersion = "1.18"
    authors = listOf("My-Name-Is-Jeff")
    depend = listOf("BetterTeams")
    libraries = listOf("net.axay:kspigot:1.18.2")
}

tasks {
    processResources {
        duplicatesStrategy = DuplicatesStrategy.EXCLUDE
        filteringCharset = "UTF-8"
    }

    javadoc {
        options.encoding = "UTF-8"
    }

    assemble {
        dependsOn(reobfJar)
    }

    withType<JavaCompile> {
        options.encoding = "UTF-8"
    }

    withType<KotlinCompile> {
        kotlinOptions {
            jvmTarget = "17"
            freeCompilerArgs =
                listOf("-opt-in=kotlin.RequiresOptIn", "-Xjvm-default=all", "-Xrelease=17", "-Xbackend-threads=0")
        }
        kotlinDaemonJvmArguments.set(
            listOf(
                "-Xmx2G",
            )
        )
    }

    named<Jar>("jar") {
        enabled = false
        dependsOn(shadowJar)
    }

    withType<ShadowJar> {
        configurations = listOf(shadowMe)
        archiveFileName.set(jar.get().archiveFileName)
    }
}

kotlin {
    jvmToolchain {
        languageVersion.set(JavaLanguageVersion.of(17))
    }
}