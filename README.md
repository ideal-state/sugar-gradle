# Sugar Gradle

<img src="./.idea/icon.png" alt="Sugar Gradle LOGO" width="" height="auto"></img>

[![Gradle](https://img.shields.io/badge/Gradle-8%2E9-g?logo=gradle&style=flat-square)](https://gradle.org/)
[![Zulu JDK](https://img.shields.io/badge/Zulu%20JDK-8-blue?style=flat-square)](https://www.azul.com/downloads/?package=jdk#zulu)
![GitHub code size in bytes](https://img.shields.io/github/languages/code-size/ideal-state/sugar-gradle?style=flat-square&logo=github)
[![Discord](https://img.shields.io/discord/1191122625389396098?style=flat-square&logo=discord)](https://discord.gg/DdGhNzAu2r)
![GitHub Actions Workflow Status](https://img.shields.io/github/actions/workflow/status/ideal-state/sugar-gradle/release.yml?style=flat-square)
![GitHub Release](https://img.shields.io/github/v/release/ideal-state/sugar-gradle?style=flat-square)

<a href="https://github.com/ideal-state/sugar-gradle/graphs/contributors">
  <img src="https://contrib.rocks/image?repo=ideal-state/sugar-gradle" alt="contributor" width="36px" height="auto" />
</a>

### 简介

> 详情见 [Gradle Plugin Central](https://plugins.gradle.org/plugin/team.idealstate.sugar.gradle.plugin)

### 如何使用

#### Gradle
```groovy
// build.gradle
plugins {
    id "team.idealstate.sugar.gradle.plugin" version "${version}"
}
```

```kotlin
// build.gradle.kts
plugins {
    id("team.idealstate.sugar.gradle.plugin") version "$version"
}
```

### 如何构建

```shell
# 1. 克隆项目到本地
git clone https://github.com/ideal-state/sugar-gradle.git
# 2. 进入项目根目录
cd ./sugar-gradle
# 3. 构建项目
./gradlew jar
```

### 怎样成为贡献者

在贡献之前，你需要了解[相应的规范](https://github.com/ideal-state/.github/blob/main/profile/README.md)。
