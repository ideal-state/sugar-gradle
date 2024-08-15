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

package team.idealstate.sugar.gradle.plugin

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.initialization.Settings

open class SugarGradlePlugin: Plugin<Any> {
    override fun apply(target: Any) {
        when (target) {
            is Settings -> {
                target.plugins.apply(SugarGradleSettingsPlugin::class.java)
            }

            is Project -> {
                target.plugins.apply(SugarGradleProjectPlugin::class.java)
            }

            else -> {
                throw UnsupportedOperationException("unsupported target type: ${target.javaClass.name}")
            }
        }
    }
}