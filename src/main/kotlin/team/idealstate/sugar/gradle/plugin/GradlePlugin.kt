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

import org.gradle.api.Project

/**
 * @date 2024/3/19 17:45
 * @author ketikai
 * @since 1.0.0
 */
abstract class GradlePlugin : org.gradle.api.Plugin<Project> {

    private var applied: Project? = null
        set(value) {
            if (field != null) {
                throw IllegalStateException("already applied to ${field!!.name}")
            }
            field = value
        }
    protected val project: Project
        get() = applied!!
    private val depends: MutableSet<String> = linkedSetOf()
    protected val metadata: Metadata by lazy {
        Metadata.of(javaClass)
    }

    final override fun apply(target: Project) {
        applied = target
        applyDepends()
        apply()
    }

    protected abstract fun apply()

    protected fun dependsOn(vararg pluginId: String) {
        depends.addAll(pluginId)
    }

    private fun applyDepends() {
        val plugins = project.plugins
        for (depend in depends) {
            if (plugins.hasPlugin(depend)) {
                continue
            }
            plugins.apply(depend)
        }
    }
}