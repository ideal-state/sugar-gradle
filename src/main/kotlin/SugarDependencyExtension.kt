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

import org.gradle.api.plugins.ObjectConfigurationAction
import org.gradle.plugin.use.PluginDependenciesSpec
import org.gradle.plugin.use.PluginDependencySpec
import team.idealstate.sugar.gradle.plugin.Metadata
import team.idealstate.sugar.gradle.plugin.SugarGradlePlugin
import team.idealstate.sugar.gradle.plugin.java.JavaGradlePlugin
import team.idealstate.sugar.gradle.plugin.publish.MavenPublishGradlePlugin
import team.idealstate.sugar.gradle.plugin.repository.RepositoryGradlePlugin

/**
 * @date 2024/3/20 22:50
 * @author ketikai
 * @since 1.0.0
 */
val PluginDependenciesSpec.sugar_gradle: PluginDependencySpec
    get() = id(Metadata.of(SugarGradlePlugin::class.java).id)

val PluginDependenciesSpec.sugar_repository: PluginDependencySpec
    get() = id(Metadata.of(RepositoryGradlePlugin::class.java).id)

val PluginDependenciesSpec.sugar_java: PluginDependencySpec
    get() = id(Metadata.of(JavaGradlePlugin::class.java).id)

val PluginDependenciesSpec.sugar_publish: PluginDependencySpec
    get() = id(Metadata.of(MavenPublishGradlePlugin::class.java).id)

val ObjectConfigurationAction.sugar_gradle: ObjectConfigurationAction
    get() {
        plugin(SugarGradlePlugin::class.java)
        return this
    }

val ObjectConfigurationAction.sugar_repository: ObjectConfigurationAction
    get() {
        plugin(RepositoryGradlePlugin::class.java)
        return this
    }

val ObjectConfigurationAction.sugar_java: ObjectConfigurationAction
    get() {
        plugin(JavaGradlePlugin::class.java)
        return this
    }

val ObjectConfigurationAction.sugar_publish: ObjectConfigurationAction
    get() {
        plugin(MavenPublishGradlePlugin::class.java)
        return this
    }
