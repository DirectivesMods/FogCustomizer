import dev.architectury.pack200.java.Pack200Adapter
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

plugins {
    id("java")
    id("gg.essential.loom") version "1.6.+"
    id("net.kyori.blossom") version "1.3.1"
    id("dev.architectury.architectury-pack200") version "0.1.3"
}

val projectName: String by project
val projectId: String by project
val projectGroup: String by project
val projectVersionRaw = property("projectVersion")?.toString()
    ?: error("projectVersion is not set")

val mcVersion = property("minecraft.version")?.toString()
    ?: error("minecraft.version is not set")
val forgeVersion = property("forge.version")?.toString()
    ?: error("forge.version is not set")
val mcpMappings = property("mcp.mappings")?.toString()
    ?: error("mcp.mappings is not set")

val projectDescription = findProperty("projectDescription")?.toString().orEmpty()
val projectUrl = findProperty("projectUrl")?.toString().orEmpty()
val projectUpdateUrl = findProperty("projectUpdateUrl")?.toString().orEmpty()

val effectiveVersion = if (projectVersionRaw.endsWith("-dev")) {
    val suffix = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"))
    "$projectVersionRaw-$suffix"
} else {
    projectVersionRaw
}

group = projectGroup
version = effectiveVersion

base {
    archivesName.set(projectId)
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(8))
    }
    sourceCompatibility = JavaVersion.VERSION_1_8
    targetCompatibility = JavaVersion.VERSION_1_8
    withSourcesJar()
}

loom {
    silentMojangMappingsLicense()

    forge {
        pack200Provider.set(Pack200Adapter())
    }

    runs {
        named("client") {
            ideConfigGenerated(true)
        }
    }
}

blossom {
    replaceToken("@MOD_VERSION@", effectiveVersion)
}

repositories {
    ivy {
        name = "localEssentialLoomForgeCache"
        url = uri(File(gradle.gradleUserHomeDir, "caches/essential-loom/$mcVersion/forge"))
        patternLayout {
            artifact("[revision]/[artifact](-[classifier]).[ext]")
        }
        metadataSources {
            artifact()
        }
        content {
            includeModule("net.minecraftforge", "forge")
        }
    }
    ivy {
        name = "localPolyfrostLoomForgeCache"
        url = uri(File(gradle.gradleUserHomeDir, "caches/polyfrost-loom/$mcVersion/forge"))
        patternLayout {
            artifact("[revision]/[artifact](-[classifier]).[ext]")
        }
        metadataSources {
            artifact()
        }
        content {
            includeModule("net.minecraftforge", "forge")
        }
    }
    ivy {
        name = "localFabricLoomForgeCache"
        url = uri(File(gradle.gradleUserHomeDir, "caches/fabric-loom/$mcVersion/legacyforge"))
        patternLayout {
            artifact("[revision]/[artifact](-[classifier]).[ext]")
        }
        metadataSources {
            artifact()
        }
        content {
            includeModule("net.minecraftforge", "forge")
        }
    }
    mavenCentral()
    maven("https://repo.polyfrost.cc/releases")
}

dependencies {
    minecraft("com.mojang:minecraft:$mcVersion")
    mappings("de.oceanlabs.mcp:mcp_stable:$mcpMappings")
    forge("net.minecraftforge:forge:$forgeVersion")

    compileOnly("cc.polyfrost:oneconfig-1.8.9-forge:0.2.2-alpha223")
    include("cc.polyfrost:oneconfig-wrapper-launchwrapper:1.0.0-beta17")
}

configurations.configureEach {
    resolutionStrategy.eachDependency {
        if (requested.group == "cpw.mods" && requested.name == "grossjava9hacks" && requested.version == "1.3.+") {
            useVersion("1.3.3")
            because("1.3.3 is already cached locally; pinning avoids remote metadata lookup for the dynamic Forge dependency")
        }
    }
}

tasks {
    withType<JavaCompile>().configureEach {
        options.encoding = "UTF-8"
    }

    processResources {
        exclude("**/*.xcf")

        val metadata = mapOf(
            "mod_version" to effectiveVersion,
            "minecraft_version" to mcVersion,
            "project_description" to projectDescription,
            "project_url" to projectUrl,
            "project_update_url" to projectUpdateUrl,
        )

        inputs.properties(metadata)

        filesMatching("mcmod.info") {
            expand(metadata)
        }
    }

    jar {
        duplicatesStrategy = DuplicatesStrategy.EXCLUDE
        manifest {
            attributes(
                "ModSide" to "CLIENT",
                "ForceLoadAsMod" to true,
                "TweakOrder" to "0",
                "TweakClass" to "cc.polyfrost.oneconfig.loader.stage0.LaunchWrapperTweaker",
            )
        }
    }
}

val localInstallScript = file("install.local.gradle.kts")
if (localInstallScript.exists()) {
    apply(from = localInstallScript)
}
