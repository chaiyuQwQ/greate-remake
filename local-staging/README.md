# local-staging —— 本地 Maven 布局模板

用于把**手工下载**的制品喂给 Gradle，而不改动 `build.gradle` 的官方仓库配置。

## 目录即 Maven 布局

```
local-staging/
└── neoform-runtime-1.0.43/
    ├── neoform-runtime-1.0.43.pom
    ├── neoform-runtime-1.0.43.module
    └── （把 neoform-runtime-1.0.43-all.jar 复制进来，命名 neoform-runtime-1.0.43.jar）
```

## 注入方式（不改 build.gradle）

```groovy
// local-repo.init.gradle
allprojects {
    repositories {
        maven { name = "localStaged"; url = uri("file:///<绝对路径>/local-staging") }
    }
}
```

```bash
./gradlew build --init-script local-repo.init.gradle
```

init script 在 `build.gradle` 之前求值，所以本地仓库排在第一位，不会先等远程超时。

## 为什么要带 .module

`neoform-runtime` 必须提供 capability `net.neoforged:neoform-runtime-external-tools`，
否则 moddev 的 `neoFormRuntimeExternalTools` 配置解析失败。
另外 POM 必须包含这行注释，否则 Gradle 会无视 `.module`：

```xml
<!-- do_not_remove: published-with-gradle-metadata -->
```

详见 `../OFFLINE-STAGING.md` §4。
