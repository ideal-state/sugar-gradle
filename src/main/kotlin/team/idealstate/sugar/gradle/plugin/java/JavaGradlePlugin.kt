/*
 *    Copyright 2024 ideal-state
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package team.idealstate.sugar.gradle.plugin.java

import org.gradle.api.JavaVersion
import org.gradle.api.plugins.JavaPluginExtension
import org.gradle.api.tasks.Copy
import org.gradle.api.tasks.bundling.Jar
import org.gradle.api.tasks.compile.JavaCompile
import org.gradle.api.tasks.javadoc.Javadoc
import org.gradle.external.javadoc.StandardJavadocDocletOptions
import org.gradle.jvm.toolchain.JavaLanguageVersion
import org.gradle.jvm.toolchain.JvmVendorSpec
import org.gradle.language.jvm.tasks.ProcessResources
import team.idealstate.sugar.gradle.plugin.ConfigSupport
import team.idealstate.sugar.gradle.plugin.ConfigurableGradlePlugin
import team.idealstate.sugar.gradle.plugin.Metadata
import team.idealstate.sugar.gradle.plugin.SugarGradlePlugin
import team.idealstate.sugar.gradle.plugin.java.config.JavaConfig
import team.idealstate.sugar.gradle.plugin.repository.RepositoryGradlePlugin
import java.io.File
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

/**
 * @date 2024/3/19 15:56
 * @author ketikai
 * @since 1.0.0
 */
@Metadata("team.idealstate.sugar.gradle.plugin.java", "sugar-gradle", "java")
open class JavaGradlePlugin : ConfigurableGradlePlugin<JavaConfig>(
    ConfigSupport.TOML,
    DEFAULT_CONFIG_NAME,
    JavaConfig::class.java
) {

    init {
        super.dependsOn(
            Metadata.of(SugarGradlePlugin::class.java).id,
            Metadata.of(RepositoryGradlePlugin::class.java).id,
            "java",
            "java-library"
        )
    }

    override fun apply() {
        configureJavaPluginExtension()

        val encoding = config.file.encoding
        configureJavaCompileTask(encoding)
        configureProcessResourcesTask()
        configureJavadocTask(encoding)
        configureSourcesJarTask()
        configureJavadocJarTask()
        configureJarTask()
    }

    private fun configureJarTask() {
        project.tasks.named("jar", Jar::class.java) {
            configureJarTask(it)
        }
    }

    private fun configureSourcesJarTask() {
        project.tasks.named("sourcesJar", Jar::class.java) {
            configureJarTask(it)
        }
    }

    private fun configureJavadocJarTask() {
        project.tasks.named("javadocJar", Jar::class.java) {
            configureJarTask(it)
        }
    }

    private fun configureJarTask(jarTask: Jar) {
        jarTask.dependsOn(
            "copyCopyright",
            "copyDependencyCopyright"
        )
        jarTask.from(project.tasks.named("processResources", ProcessResources::class.java))
        jarTask.manifest.attributes.putAll(mapOf(
            "group" to project.group,
            "name" to project.name,
            "version" to project.version,
            "java-language-version" to config.language.version,
            "build-date" to ZonedDateTime.now().format(DateTimeFormatter.ISO_ZONED_DATE_TIME)
        ))
    }

    private fun configureJavaPluginExtension() {
        val java = config
        val javaLanguageVersion = java.language.version
        project.extensions.getByType(JavaPluginExtension::class.java).apply {
            sourceCompatibility = JavaVersion.toVersion(javaLanguageVersion)
            targetCompatibility = sourceCompatibility
            toolchain { toolchain ->
                toolchain.languageVersion.set(JavaLanguageVersion.of(javaLanguageVersion))
                toolchain.vendor.set(JvmVendorSpec.AZUL)
            }
            withSourcesJar()
            withJavadocJar()
        }
    }

    private fun configureJavaCompileTask(encoding: String) {
        project.tasks.withType(JavaCompile::class.java) {
            it.options.also { compileOptions ->
                compileOptions.isFork = true
                compileOptions.encoding = encoding
                compileOptions.compilerArgs.also { compileArgs ->
//                    compileArgs.add("-deprecation")
//                    compileArgs.add("-XDignore.symbol.file")
                    compileArgs.add("-parameters")
                }
                compileOptions.forkOptions.also { forkOptions ->
                    forkOptions.jvmArgs!!.add("-J-Dfile.encoding=$encoding")
                    forkOptions.executable =
                        it.javaCompiler.get().executablePath.asFile.absolutePath
                }
            }
        }
    }

    private fun configureProcessResourcesTask() {
        val destCopyrightDir = "${project.projectDir}/build/resources/main/META-INF/COPYRIGHT/"
        project.tasks.create("copyCopyright", Copy::class.java) {
            it.mustRunAfter(project.tasks.named("processResources", ProcessResources::class.java))
            it.from("${project.projectDir}/LICENSE.txt", "${project.projectDir}/NOTICE.txt")
            it.into(destCopyrightDir)
        }
        project.tasks.create("copyDependencyCopyright", Copy::class.java) {
            it.mustRunAfter(project.tasks.named("processResources", ProcessResources::class.java))
            it.from("${project.projectDir}/LICENSES/")
            it.into("${destCopyrightDir}LICENSES/")
        }
//        project.tasks.named("processResources", ProcessResources::class.java) {
//
//        }
    }

    private fun copyFile(source: File, destinationDir: File) {
        if (!source.exists()) {
            return
        }
        if (!destinationDir.exists()) {
            destinationDir.mkdirs()
        }
        require(destinationDir.isDirectory) { "invalid destination directory: '${destinationDir}'" }
        if (source.isFile) {
            source.copyTo(File(destinationDir, source.name), true)
            return
        }
        val destDir = File(destinationDir, source.name)
        destDir.mkdirs()
        source.listFiles()?.forEach { file ->
            if (file.isDirectory) {
                copyFile(file, destDir)
            } else {
                file.copyTo(File(destDir, file.name), true)
            }
        }
    }

    private fun configureJavadocTask(encoding: String) {
        project.tasks.withType(Javadoc::class.java) {
            it.options { options ->
                options as StandardJavadocDocletOptions
                options.charSet(encoding)
                options.encoding(encoding)
                options.docEncoding(encoding)
                options.locale("zh_CN")
                options.windowTitle("${project.name}-${project.version} API")
                options.docTitle(options.windowTitle)
                options.author(true)
                options.version(true)
                options.jFlags("-Dfile.encoding=${encoding}")
            }
            it.isFailOnError = false
        }
    }
}