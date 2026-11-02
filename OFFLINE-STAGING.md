# 离线 / 半离线构建说明

> 用途：当 `maven.neoforged.net` 不可达，或想复用别人已下载好的制品时，怎么把
> NeoForge 构建链喂饱。**正常联网环境不需要看这个文件。**

---

## 1. 先分清三种"NeoForge 包"

很多人会拿错，这三者不能互相替代：

| 文件 | 是什么 | Gradle 构建能用吗 |
|---|---|---|
| `neoforge-21.1.219-installer.jar` | **安装器**。给人用的 GUI/CLI，把 NeoForge 装进 `.minecraft`。内部只有 `install_profile.json`、`version.json`、`data/client.lzma`、`data/server.lzma` 和一堆 gson/jopt 类 | ❌ **不能**。它不含 `userdev`，也没有 Maven 布局 |
| `neoforge-21.1.219-universal.jar` | 运行期用的模组本体（玩家放进 mods 的那个） | ❌ 不能单独用 |
| **`neoforge-21.1.219-userdev.jar`** | **开发用制品**。含反编译配置、AT、补丁、`moddev-config` 之类 | ✅ **这才是 Gradle 需要的** |

从安装器里能捞到的有用信息只有两个：

```json
// version.json → arguments.game
"--fml.neoFormVersion", "20240808.144430"     // → net.neoforged:neoform:1.21.1-20240808.144430
```

以及 `install_profile.json` 里的 libraries 列表（可用来核对坐标，但照抄没用）。

---

## 2. 一个版本需要哪些 NetForged 制品

以 `21.1.248` 的 Gradle 缓存为准（`21.1.219` 结构相同）：

```
net/neoforged/neoforge/21.1.219/
├── neoforge-21.1.219.pom
├── neoforge-21.1.219.module          ← 必需，含 capability 与变体
├── neoforge-21.1.219-userdev.jar     ← 必需（编译/运行的核心）
├── neoforge-21.1.219-universal.jar   ← 必需
└── neoforge-21.1.219-sources.jar     ← 可选（IDE 看源码）

net/neoforged/neoform/1.21.1-20240808.144430/
├── neoform-1.21.1-20240808.144430.pom
├── neoform-1.21.1-20240808.144430.module
└── neoform-1.21.1-20240808.144430.zip   ← MC 补丁/配置

net/neoforged/neoform-runtime/1.0.43/
├── neoform-runtime-1.0.43.pom
├── neoform-runtime-1.0.43.module
└── neoform-runtime-1.0.43-all.jar    ← 注意是 fat jar（-all）
```

下载地址模板（把版本换掉即可）：

```
https://maven.neoforged.net/releases/net/neoforged/neoforge/21.1.219/neoforge-21.1.219-userdev.jar
https://maven.neoforged.net/releases/net/neoforged/neoforge/21.1.219/neoforge-21.1.219-universal.jar
https://maven.neoforged.net/releases/net/neoforged/neoforge/21.1.219/neoforge-21.1.219.pom
https://maven.neoforged.net/releases/net/neoforged/neoforge/21.1.219/neoforge-21.1.219.module
```

---

## 3. 把文件摆成本地 Maven 仓库

```
.cache/local-maven/
└── net/neoforged/
    ├── neoforge/21.1.219/{pom,module,userdev.jar,universal.jar}
    ├── neoform/1.21.1-20240808.144430/{pom,module,zip}
    └── neoform-runtime/1.0.43/{pom,module,jar}
```

然后**不改 build.gradle**，用一个 init script 注入仓库（顺序在前，优先命中本地）：

```groovy
// local-repo.init.gradle
allprojects {
    repositories {
        maven { name = "localStaged"; url = uri("file:///C:/path/to/local-maven") }
    }
}
```

```bash
./gradlew build --init-script local-repo.init.gradle
```

init script 在 `build.gradle` 之前求值，所以本地仓库天然排在第一位，不会先等远程超时。

---

## 4. 两个坑（都实测过）

### 4.1 `neoform-runtime` 必须有 Gradle 模块元数据里的 capability

moddev 内部有个配置 `neoFormRuntimeExternalTools`，它按 **capability** 找 NFRT：

```
net.neoforged:neoform-runtime-external-tools
```

只放一个手写 POM + jar 会报：

```
Unable to find a variant with the requested capability:
coordinates 'net.neoforged:neoform-runtime-external-tools'
```

必须提供 `.module`，其中有一个变体声明该 capability：

```json
{
  "name": "externalTools",
  "attributes": { "org.gradle.category": "library", "org.gradle.usage": "java-runtime" },
  "dependencies": [ /* DiffPatch / AutoRenamingTool / jst-cli-bundle / mergetool / installertools */ ],
  "capabilities": [{ "group": "net.neoforged", "name": "neoform-runtime-external-tools", "version": "1.0.43" }]
}
```

### 4.2 POM 里必须有 `do_not_remove` 标记，否则 `.module` 会被无视

Gradle 只有在 POM 含这一行时才会优先读 `.module`：

```xml
<!-- do_not_remove: published-with-gradle-metadata -->
```

否则它会退化成"按 POM 推导变体"，于是又回到 4.1 那个 capability 报错，
而且报错里列出的变体名是 `compile` / `runtime` / `javadoc` 这种 POM 推导名——
看到这组名字，就说明 Gradle 根本没读你的 `.module`。

---

## 5. NFRT 的五个外部工具

`externalTools` 变体依赖这五个（版本取自 NFRT 2.0.31 的官方元数据）：

| 坐标 | 版本 | 分类器 |
|---|---|---|
| `io.codechicken:DiffPatch` | 2.0.0.36 | `all` |
| `net.neoforged:AutoRenamingTool` | 2.0.17 | `all` |
| `net.neoforged.jst:jst-cli-bundle` | 2.0.11 | — |
| `net.minecraftforge:mergetool` | 1.1.7 | `fatjar` |
| `net.neoforged.installertools:installertools` | 4.0.12 | `fatjar` |

都是自包含的 fat jar（所以才有分类器）。缺任何一个，`createMinecraftArtifacts` 都过不去。
如果 NFRT 版本不同，这五个的版本要求也可能不同，以你本地 `.module` 里的为准。

---

## 6. 还需要什么（不由 Maven 提供）

NFRT 会自己去拉：

- Minecraft `client.jar` / `server.jar` / `client_mappings`：`piston-meta.mojang.com` → `piston-data.mojang.com`
- Minecraft 依赖库：`libraries.minecraft.net`
- `runClient` 的 assets：`resources.download.minecraft.net`

这几项**不能用本地 Maven 替换**，只能保证网络可达，或者预先准备好
`~/.gradle/caches/neoformruntime/` 下的缓存。

---

## 7. 判断"到底缺什么"的最快方法

```bash
./gradlew createMinecraftArtifacts --info 2>&1 | grep -E "Could not resolve|Unable to find"
```

报错里第一条 `Could not resolve <坐标>` 就是当前缺口，逐个补即可，
不用一次猜全。
