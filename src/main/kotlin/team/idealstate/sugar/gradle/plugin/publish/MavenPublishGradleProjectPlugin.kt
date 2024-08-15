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

package team.idealstate.sugar.gradle.plugin.publish

import groovy.util.Node
import org.gradle.api.artifacts.Configuration
import org.gradle.api.publish.PublishingExtension
import org.gradle.api.publish.maven.MavenPublication
import org.gradle.plugins.signing.SigningExtension
import team.idealstate.sugar.gradle.plugin.ConfigSupport
import team.idealstate.sugar.gradle.plugin.ConfigurableGradleProjectPlugin
import team.idealstate.sugar.gradle.plugin.PluginMetadata
import team.idealstate.sugar.gradle.plugin.SugarGradleProjectPlugin
import team.idealstate.sugar.gradle.plugin.java.JavaGradleProjectPlugin
import team.idealstate.sugar.gradle.plugin.publish.config.PublicationConfig

@PluginMetadata("team.idealstate.sugar.gradle.plugin.publish", "sugar-gradle", "publish")
open class MavenPublishGradleProjectPlugin : ConfigurableGradleProjectPlugin<PublicationConfig>(
    ConfigSupport.TOML,
    DEFAULT_CONFIG_NAME,
    PublicationConfig::class.java
) {

    init {
        dependsOn(
            PluginMetadata.of(SugarGradleProjectPlugin::class.java).id,
            PluginMetadata.of(JavaGradleProjectPlugin::class.java).id,
            "maven-publish"
        )
    }

    @Suppress("DuplicatedCode")
    override fun apply() {
        val publishingExtension = project.extensions
            .getByType(PublishingExtension::class.java)
        publishingExtension.repositories { handler ->
            handler.maven {
                it.name = "Build"
                it.url = project.uri("file://${project.projectDir}/build/repository")
            }
            handler.mavenLocal()
        }
        val publications = publishingExtension.publications
        val sugarPublication = publications.create(
            "sugar",
            MavenPublication::class.java
        ) { publication ->
            publication.groupId = project.group.toString()
            publication.artifactId = project.name
            publication.version = project.version.toString()
            publication.from(project.components.getByName("java"))

            publication.pom { pom ->
                pom.name.set(project.name)
                config.description?.let {
                    pom.description.set(it)
                }
                pom.packaging = "jar"
                config.url?.let {
                    pom.url.set(it.toString())
                }
                config.inceptionYear?.let {
                    pom.inceptionYear.set(it)
                }

                config.organization?.let {
                    pom.organization { organization ->
                        organization.name.set(it.name)
                        organization.url.set(it.url.toString())
                    }
                }

                config.developers?.forEach {
                    pom.developers { developers ->
                        developers.developer { developer ->
                            developer.id.set(it.id)
                            developer.name.set(it.name)
                            developer.email.set(it.email)
                        }
                    }
                }

                config.license?.let {
                    pom.licenses { pomLicense ->
                        pomLicense.license { license ->
                            license.name.set(it.name)
                            license.url.set(it.url.toString())
                        }
                    }
                }

                config.scm?.let {
                    pom.scm { pomScm ->
                        pomScm.url.set(it.url.toString())
                        pomScm.connection.set(it.connection.toString())
                        pomScm.developerConnection.set(it.developerConnection.toString())
                    }
                }

                pom.withXml { xml ->
                    var dependenciesNode: Node? = null
                    val compileDependencyIds = mutableSetOf<String>()
                    var scope = "compile"
                    for (dependency in
                    getDependencies(
                        project.configurations.getByName(
                            "compileClasspath"
                        )
                    )
                    ) {
                        val group = dependency["group"]!!
                        val name = dependency["name"]!!
                        val version = dependency["version"]!!
                        val id = "${group}:${name}:${version}"
                        if (dependenciesNode == null) {
                            dependenciesNode = xml.asNode().appendNode("dependencies")
                        }
                        val dependencyNode = dependenciesNode!!.appendNode("dependency")
                        dependencyNode.appendNode("groupId", group)
                        dependencyNode.appendNode("artifactId", name)
                        dependencyNode.appendNode("version", version)
                        dependencyNode.appendNode("scope", scope)
                        compileDependencyIds.add(id)
                    }
                    scope = "runtime"
                    for (dependency in
                    getDependencies(
                        project.configurations.getByName(
                            "runtimeClasspath"
                        )
                    )
                    ) {
                        val group = dependency["group"]!!
                        val name = dependency["name"]!!
                        val version = dependency["version"]!!
                        val id = "${group}:${name}:${version}"
                        if (!compileDependencyIds.contains(id)) {
                            if (dependenciesNode == null) {
                                dependenciesNode = xml.asNode().appendNode("dependencies")
                            }
                            val dependencyNode = dependenciesNode!!.appendNode("dependency")
                            dependencyNode.appendNode("groupId", group)
                            dependencyNode.appendNode("artifactId", name)
                            dependencyNode.appendNode("version", version)
                            dependencyNode.appendNode("scope", scope)
                            compileDependencyIds.add(id)
                        }
                    }
                }
            }
        }
        tryConfigureSigning(sugarPublication)
    }

    private fun tryConfigureSigning(vararg publications: org.gradle.api.publish.Publication) {
        val executable = project.property("signing.gnupg.executable") as String?
        val keyName = project.property("signing.gnupg.keyName") as String?
        val passphrase = project.property("signing.gnupg.passphrase") as String?
        if (executable != null && keyName != null && passphrase != null) {
            project.plugins.apply("signing")
            project.extensions.getByType(SigningExtension::class.java).also {
                it.useGpgCmd()
                it.sign(*publications)
            }
        }
    }

    companion object {

        @JvmStatic
        fun getDependencies(configuration: Configuration): LinkedHashSet<Map<String, String>> {
            val dependencies = configuration.resolvedConfiguration.firstLevelModuleDependencies
            val ids = LinkedHashSet<Map<String, String>>(dependencies.size)
            for (dependency in dependencies) {
                ids.add(
                    linkedMapOf(
                        "group" to dependency.moduleGroup,
                        "name" to dependency.moduleName,
                        "version" to dependency.moduleVersion,
                    )
                )
            }
            return ids
        }
    }
}