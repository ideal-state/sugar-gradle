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

import java.io.File

abstract class ConfigurableGradleProjectPlugin<C>(
    private val configSupport: ConfigSupport,
    private val configName: String,
    private val configType: Class<C>
) : GradleProjectPlugin() {

    val config: C by lazy {
        configSupport
            .build()
            .readValue(resource("$configName.${configSupport.extension()}"), configType)
    }

    protected fun resource(path: String): File {
        val resourceGroup = if (metadata.group == metadata.name) {
            "${metadata.name}/"
        } else {
            "${metadata.group}/${metadata.name}/"
        }
        @Suppress("NAME_SHADOWING")
        val path = "$resourceGroup${path.replace('\\', '/')}"
        val file = project.file(path)
        if (!file.exists()) {
            val parentFile = file.parentFile
            if (!parentFile.exists()) {
                parentFile.mkdirs()
            }
            val resourcePath = "/$RESOURCES_ROOT_DIR/$path"
            val resourceStream = javaClass.getResourceAsStream(resourcePath)
                ?: throw IllegalStateException("resource not found: '$resourcePath'")
            file.outputStream().use { output ->
                resourceStream.use { input ->
                    val buffer = ByteArray(1024)
                    while (true) {
                        val read = input.read(buffer)
                        if (read == -1) break
                        output.write(buffer, 0, read)
                    }
                }
                output.flush()
            }
        }
        return file
    }

    companion object {
        @JvmStatic
        protected val RESOURCES_ROOT_DIR = "resources"

        @JvmStatic
        protected val DEFAULT_CONFIG_NAME = "config"
    }
}