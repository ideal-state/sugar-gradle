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

package team.idealstate.sugar.gradle.plugin.repository

import team.idealstate.sugar.gradle.plugin.ConfigSupport
import team.idealstate.sugar.gradle.plugin.ConfigurableGradlePlugin
import team.idealstate.sugar.gradle.plugin.PluginMetadata
import team.idealstate.sugar.gradle.plugin.SugarGradlePlugin
import team.idealstate.sugar.gradle.plugin.repository.config.Repository
import team.idealstate.sugar.gradle.plugin.repository.config.RepositoryConfig

@PluginMetadata("team.idealstate.sugar.gradle.plugin.repository", "sugar-gradle", "repository")
open class RepositoryGradlePlugin : ConfigurableGradlePlugin<RepositoryConfig>(
    ConfigSupport.TOML,
    DEFAULT_CONFIG_NAME,
    RepositoryConfig::class.java
) {

    init {
        dependsOn(
            PluginMetadata.of(SugarGradlePlugin::class.java).id
        )
    }

    @Suppress("DuplicatedCode")
    override fun apply() {
        project.repositories.also { handler ->
            handler.mavenLocal()
            for (repository in config.repositories) {
                when (repository.type) {
                    Repository.Type.MAVEN -> handler.maven {
                        it.name = repository.name
                        it.url = repository.url
                        if (repository.username != null && repository.password != null) {
                            it.credentials { credentials ->
                                credentials.username = repository.username
                                credentials.password = repository.password
                            }
                        }
                    }

                    Repository.Type.IVY -> handler.ivy {
                        it.name = repository.name
                        it.url = repository.url
                        if (repository.username != null && repository.password != null) {
                            it.credentials { credentials ->
                                credentials.username = repository.username
                                credentials.password = repository.password
                            }
                        }
                    }
                }
            }
            handler.mavenCentral()
        }
    }
}