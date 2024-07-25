plugins {
    id("idea")
    id("java-gradle-plugin")
    id("com.gradle.plugin-publish") version "1.2.1"
    kotlin("jvm") version "1.9.20"
    id("org.jetbrains.dokka") version "1.9.20"
}

group = "team.idealstate.sugar"
version = "1.0.0"

val javaLanguageVersion = 8
idea {
    project {
        languageLevel.level = javaLanguageVersion.toString()
    }
}

kotlin {
    coreLibrariesVersion = "1.9.20"
    jvmToolchain {
        languageVersion.set(JavaLanguageVersion.of(javaLanguageVersion))
        vendor.set(JvmVendorSpec.AZUL)
    }
    compilerOptions.javaParameters.set(true)
}

repositories {
    mavenLocal()
    gradlePluginPortal()
    maven {
        name = "sonatype-public"
        url = uri("https://oss.sonatype.org/content/groups/public/")
    }
    mavenCentral()
}

dependencies {
    compileOnly(gradleApi())
    val jacksonVersion = "2.17.0"
    implementation("com.fasterxml.jackson.dataformat:jackson-dataformat-toml:$jacksonVersion")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin:$jacksonVersion")
}

configurations {
    api {
        dependencies.remove(project.dependencies.gradleApi())
    }
}

gradlePlugin {
    val repoUrl = "https://github.com/ideal-state/sugar"
    website.set(repoUrl)
    vcsUrl.set(repoUrl)
    plugins {
        create("SugarPlugin") {
            id = "team.idealstate.sugar.gradle.plugin"
            implementationClass = "team.idealstate.sugar.gradle.plugin.SugarGradlePlugin"
            displayName = "Sugar Plugin"
            description = "Gradle plugin. Provide default configuration alternatives to build scripts to minimize the use of build scripts during project development. Including but not limited to 'repository', 'java', 'maven-publish' and other related configurations."
            tags.set(listOf("java", "project", "sugar", "configuration", "publish", "sonatype central portal"))
        }
    }
}

publishing {
    repositories {
        maven {
            name = "build"
            url = uri("file://${projectDir}/build/repository/")
        }
    }
}

tasks.processResources {
    duplicatesStrategy = DuplicatesStrategy.WARN
}

val destCopyrightDir = "${projectDir}/build/resources/main/META-INF/COPYRIGHT/"
tasks.create<Copy>("copyCopyright") {
    mustRunAfter(tasks.processResources)
    from("${projectDir}/LICENSE.txt", "${projectDir}/NOTICE.txt")
    into(destCopyrightDir)
}

tasks.create<Copy>("copyDependencyCopyright") {
    mustRunAfter(tasks.processResources)
    from("${projectDir}/LICENSES/")
    into("${destCopyrightDir}LICENSES/")
}

tasks.jar {
    dependsOn("copyCopyright", "copyDependencyCopyright")
}

tasks.create<Jar>("sourcesJar") {
    group = "build"
    dependsOn("copyCopyright", "copyDependencyCopyright")
    archiveClassifier.set("sources")
    from(sourceSets.main.get().kotlin, tasks.processResources)
}

tasks.create<Jar>("javadocJar") {
    group = "build"
    dependsOn("copyCopyright", "copyDependencyCopyright")
    archiveClassifier.set("javadoc")
    from(tasks.dokkaHtml, tasks.processResources)
}