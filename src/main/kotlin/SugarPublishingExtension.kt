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

import org.gradle.api.Action
import org.gradle.api.Project
import org.gradle.api.artifacts.dsl.RepositoryHandler
import org.gradle.api.artifacts.repositories.MavenArtifactRepository
import org.gradle.api.publish.PublicationContainer
import org.gradle.api.publish.maven.MavenPublication
import java.net.URI


val PublicationContainer.sugarPublication: MavenPublication
    get() = named("sugar", MavenPublication::class.java).get()

fun MavenArtifactRepository.login() {
    val id = name.replace(' ', '-').lowercase()
    credentials {
        it.username = System.getProperty(
            "sugar.publish.$id.key"
        )
        it.password = System.getProperty(
            "sugar.publish.$id.secret"
        )
    }
}

fun MavenArtifactRepository.login(project: Project) {
    val id = name.replace(' ', '-').lowercase()
    credentials {
        it.username = project.property(
            "sugar.publish.$id.key"
        ) as String
        it.password = project.property(
            "sugar.publish.$id.secret"
        ) as String
    }
}

fun RepositoryHandler.aliyun(): MavenArtifactRepository {
    return maven {
        it.name = "Aliyun"
        it.url = URI.create("https://maven.aliyun.com/repository/public/")
    }
}

fun RepositoryHandler.sonatype(): MavenArtifactRepository {
    return maven {
        it.name = "Sonatype"
        it.url = URI.create("https://s01.oss.sonatype.org/content/groups/public/")
    }
}

fun RepositoryHandler.sonatypeReleases(action: Action<MavenArtifactRepository> = Action {}): MavenArtifactRepository {
    return maven {
        it.name = "Sonatype-Releases"
        it.url = URI.create("https://s01.oss.sonatype.org/content/repositories/releases/")
        action.execute(it)
    }
}

fun RepositoryHandler.sonatypeSnapshots(action: Action<MavenArtifactRepository> = Action {}): MavenArtifactRepository {
    return maven {
        it.name = "Sonatype-Snapshots"
        it.url = URI.create("https://s01.oss.sonatype.org/content/repositories/snapshots/")
        action.execute(it)
    }
}

fun RepositoryHandler.sonatypeStaging(action: Action<MavenArtifactRepository>): MavenArtifactRepository {
    return maven {
        it.name = "Sonatype-Staging"
        it.url = URI.create("https://s01.oss.sonatype.org/service/local/staging/deploy/maven2/")
        action.execute(it)
    }
}