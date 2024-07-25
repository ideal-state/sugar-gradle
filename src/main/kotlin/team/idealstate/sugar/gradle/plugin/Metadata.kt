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

import kotlin.reflect.KClass

@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
@MustBeDocumented
annotation class Metadata(
    val id: String,
    val group: String,
    val name: String
) {
    companion object {
        @JvmStatic
        fun of(type: KClass<out GradlePlugin>): Metadata {
            return of(type.java)
        }

        @JvmStatic
        fun of(type: Class<out GradlePlugin>): Metadata {
            return type.getDeclaredAnnotation(Metadata::class.java)!!
        }
    }
}
