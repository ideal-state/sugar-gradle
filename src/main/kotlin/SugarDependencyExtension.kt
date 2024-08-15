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
import team.idealstate.sugar.gradle.plugin.PluginMetadata
import team.idealstate.sugar.gradle.plugin.SugarGradleProjectPlugin
import team.idealstate.sugar.gradle.plugin.java.JavaGradleProjectPlugin
import team.idealstate.sugar.gradle.plugin.publish.MavenPublishGradleProjectPlugin
import team.idealstate.sugar.gradle.plugin.repository.RepositoryGradleProjectPlugin

val PluginDependenciesSpec.sugar_gradle: PluginDependencySpec
    get() = id(PluginMetadata.of(SugarGradleProjectPlugin::class.java).id)

val PluginDependenciesSpec.sugar_repository: PluginDependencySpec
    get() = id(PluginMetadata.of(RepositoryGradleProjectPlugin::class.java).id)

val PluginDependenciesSpec.sugar_java: PluginDependencySpec
    get() = id(PluginMetadata.of(JavaGradleProjectPlugin::class.java).id)

val PluginDependenciesSpec.sugar_publish: PluginDependencySpec
    get() = id(PluginMetadata.of(MavenPublishGradleProjectPlugin::class.java).id)

val ObjectConfigurationAction.sugar_gradle: ObjectConfigurationAction
    get() {
        plugin(SugarGradleProjectPlugin::class.java)
        return this
    }

val ObjectConfigurationAction.sugar_repository: ObjectConfigurationAction
    get() {
        plugin(RepositoryGradleProjectPlugin::class.java)
        return this
    }

val ObjectConfigurationAction.sugar_java: ObjectConfigurationAction
    get() {
        plugin(JavaGradleProjectPlugin::class.java)
        return this
    }

val ObjectConfigurationAction.sugar_publish: ObjectConfigurationAction
    get() {
        plugin(MavenPublishGradleProjectPlugin::class.java)
        return this
    }
