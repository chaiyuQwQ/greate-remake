# 构建与开发环境

**Minecraft 1.21.1 · NeoForge 21.1.219 · Create 6.0.10-281 · GTCEu Modern 7.0.2 · Java 21**

> 项目介绍见 [README.md](README.md)；本文件讲环境、构建、性能与排障。

---

## 1. 快速开始

```bash
# 需要 JDK 21（不是 17，也不是 25；Gradle 8.14.3 在 JDK 25 下会报
# "Unsupported class file major version 69"）
export JAVA_HOME=/path/to/jdk-21

./gradlew runClient     # 启动客户端
./gradlew runServer     # 启动服务端
./gradlew runData       # 数据生成 → src/generated/resources/
./gradlew build         # 产出 build/libs/greate_remake-0.1.0.jar

# 调试用：跳过主菜单直接进存档（"New World" 换成你的存档文件夹名）
./gradlew runClient -Pquickplay="New World"
```

Windows PowerShell：

```powershell
$env:JAVA_HOME="C:\Program Files\Java\jdk-21.0.12.1"
.\gradlew.bat runClient
```

---

## 2. 目录约定

```
├── README.md              项目介绍
├── BUILDING.md            本文件
├── DEPENDENCIES.md        ★ 依赖清单（坐标 / 版本 / 仓库 / 用途 / 编译或运行 / 兜底来源）
├── verify-deps.py         ★ 依赖可达性自检
├── OFFLINE-STAGING.md     半离线构建说明（installer ≠ userdev、本地 Maven 暂存）
├── local-staging/         本地 Maven 布局模板（按需启用）
├── build.gradle
├── gradle.properties
├── settings.gradle
└── src/…
```


```
src/main/
├── java/electrolyte/greate/
│   ├── Greate.java              主入口（@Mod）与注册顺序
│   ├── GreateValues.java        10 档应力常量（纯数据，无 GT 依赖）
│   ├── registry/                10 级机器 / 材料 / TagPrefix 的 Registrate 注册
│   ├── content/                 方块、方块实体、渲染器、视觉（ValueBox/BER）
│   ├── foundation/              datagen、配方桥接、gametest、JEI 兼容
│   └── infrastructure/config/   服务端配置（greate_remake-server.toml）
├── resources/
│   ├── greate_remake.mixins.json       Mixin 配置（10 条注入：动力学网络、风处理、传送带等）
│   └── assets/greate_remake/textures/  ★ 贴图放这里（手写资源）
└── templates/META-INF/neoforge.mods.toml   变量模板，构建时展开

src/generated/resources/     ★ 模型/方块状态/lang 放这里（datagen 产物）
```

### 资源该放哪：一条判据

> **`runData` 会重写 `src/generated/resources`，不会碰 `src/main/resources`。**

| 资源 | 位置 | 原因 |
|---|---|---|
| `models/item/*.json`、`models/block/*.json` | `src/generated/resources/assets/greate_remake/models/` | 本项目用 `BlockStateProvider` 生成，手写也会被覆盖 |
| `blockstates/*.json` | `src/generated/resources/assets/greate_remake/blockstates/` | 同上 |
| `lang/*.json` | `src/generated/resources/assets/greate_remake/lang/` | 由 `LanguageProvider` 生成 |
| **`textures/**/*.png`** | **`src/main/resources/assets/greate_remake/textures/`** | 手写资产 |
| `sounds.json`、`particles/*.json` | `src/main/resources/assets/greate_remake/` | 手写 |

模型 / 方块状态 / lang / 贴图均已随仓库提供，无需再从外部导入。

---

## 3. 版本矩阵

完整清单（含每条的依据、用途、编译/运行区分）见 **[DEPENDENCIES.md](DEPENDENCIES.md)**。
这里只列关键值：

| 依赖 | 值 | 依据 |
|---|---|---|
| `neo_version` | `21.1.219` | Create 6.0.10 声明 `[21.1.219,)`，这是**声明下限** |
| `create_version` | `6.0.10-281` | 6.0.10 版本行最后一个 build |
| `ponder_version` | `1.0.82+mc1.21.1` | Create jar 内 jarJar 内嵌版本，**不是近似值** |
| `flywheel_version` | `1.0.6` | 同上，Create jar 内嵌 `flywheel-neoforge-1.21.1-1.0.6.jar` |
| `registrate_version` | `MC1.21-1.3.0+67` | Create 与 GT 内嵌的同一版本 |
| `gregtech_version` | `7.0.2` | 1.21.1 线最后公开发布 |
| `gtceu_ldlib_version` | `1.0.35.a` | GT jar 内 jarJar 内嵌版本 |
| `gtceu_configuration_version` | `3.1.1-neoforge` | 3.1.0 从未发布；运行时用 GT 内嵌的 3.1.0 |
| `parchment_minecraft_version` / `mappings` | `1.21.1` / `2024.11.17` | 1.21.1 有独立发布；`2024.11.10` 只属于 `parchment-1.21` |

> 关于 Ponder 坐标：`net.createmod.ponder:ponder-neoforge` 是现行坐标，
> 老坐标 `Ponder-NeoForge-1.21.1` 最高只到 `1.0.69`，不满足 Create 6.0.10 的 `[1.0.82,)`。
> 这不是版本降级问题，是坐标迁移问题。

### 仓库地址

| 仓库 | 提供 |
|---|---|
| `https://maven.neoforged.net/releases` | NeoForge、NeoForm Runtime（构建必须可达） |
| `https://maven.createmod.net` | Create、Ponder、Flywheel |
| `https://mvn.devos.one/snapshots/` | Registrate |
| `https://maven.gtceu.com` | GTCEu |
| `https://maven.firstdarkdev.xyz/snapshots` | LDLib |
| `https://repo.repsy.io/mvn/toma/public/` | Configuration |
| `https://maven.blamejared.com` | JEI |
| `https://maven.parchmentmc.org` | Parchment 映射 |
| Mojang：`piston-meta` / `piston-data` / `libraries.minecraft.net` | MC 本体与库（构建必须可达） |

---

## 4. 开工前自检

```bash
python verify-deps.py                 # 逐条 HEAD 请求，打印 OK / FAIL
```

它会把 DEPENDENCIES.md 里 A、B 两组共 19 个坐标全部打一遍。
**FAIL 的行就是需要你手工提供的**，或说明该站点在你的网络下需要单独处理。

参考结果（在受限网络下的实测，仅用于说明脚本可用性）：

| 站点 | 结果 |
|---|---|
| services.gradle.org / plugins.gradle.org | OK |
| maven.neoforged.net | 该网络下超时（**你的环境应可达**） |
| maven.createmod.net（Create/Ponder/Flywheel） | OK |
| maven.gtceu.com | OK |
| maven.firstdarkdev.xyz（LDLib） | OK |
| repo.repsy.io（Configuration） | OK |
| maven.parchmentmc.org / piston-meta.mojang.com | OK |
| mvn.devos.one（Registrate） | TLS 握手超时 —— **唯一需要留意的第三方站点**，见 DEPENDENCIES.md §E |

自检通过后：

```bash
export JAVA_HOME=/path/to/jdk-21
./gradlew dependencies --configuration compileClasspath
./gradlew build
```

### 首次构建常见失败与对策

| 现象 | 原因 | 对策 |
|---|---|---|
| `Unsupported class file major version 69` | 用了 JDK 25 跑 Gradle 8.14.3 | 换 JDK 21 |
| NeoForm Runtime 下载超时 | `maven.neoforged.net` 或 Mojang 不可达 | 这是构建的硬前提，必须打通 |
| `Could not find ponder-neoforge:1.0.87+...` | 用了老坐标或自选版本 | 用 `1.0.82+mc1.21.1`，与内嵌一致 |
| `NoClassDefFoundError: com/tterrag/registrate/...` | 用了 `:slim` 但漏声明内嵌库 | 补 B2–B5 四项 |
| Parchment zip 404 | 用了 `parchment-1.21.1:2024.11.10` | 该版本只在 `parchment-1.21` 下，改用 `2024.11.17` |

---

## 5. 运行性能（卡顿 / 不足 1 帧必读）

开发客户端在这台机器上曾经**卡在标题界面、不足 1 帧**，点不动任何按钮。下面是从定位到修复的完整结论。

### 5.1 症状

资源加载第 49 秒就结束了，之后到手动关闭的 27 秒里**日志一行不动**：

```
16:39:49.109  JEI gui atlas 创建完成   ← 加载结束
              ...27 秒零日志...
16:40:16.747  Stopping!
```

标题界面不含任何 Create/Flywheel 内容，所以这不是 mod 逻辑问题，是全局渲染管线。

### 5.2 诊断方法：采样渲染线程的调用栈

不要靠猜。用 `jstack` 反复抓 `Render thread`，看它停在哪：

```bash
# 先找到游戏进程（有 fml.modFolders 参数的那个）
jps -l
jstack <pid> | awk '/"Render thread"/{f=1} f&&/^\s+at/{print; n++} n>=8{exit}'
```

**关键判据是「采样分布」而不是单次快照**——单次快照可能是巧合，连续 8 次全在同一处才叫阻塞。

### 5.3 根因

阻塞点 100% 是这一条链：

```
Minecraft.tick()                        ← tick 被卡住，帧自然推不动
  → TextureManager.tick()
    → TextureAtlas.tick()
      → TextureAtlas.cycleAnimationFrames()
        → SpriteContents$AnimatedTexture.uploadFrame()
          → SpriteContents.upload()
            → GlStateManager._texSubImage2D()
              → GL11C.nglTexSubImage2D()      ← 卡在这个 native 调用
```

**为什么会有这么多上传？** 数一下 GT 的贴图元数据：

| 项 | 数量 |
|---|---|
| `gtceu-7.0.2.jar` 里 `.png.mcmeta` 总数 | 721 |
| 其中真正含 `animation` 段的 | **532** |
| `frametime=1`（每 tick 都传） | 92 |
| `frametime=2`（每 2 tick 传一次） | 208 |
| 对比：Create 6.0.10-281 | 14 |

GT 的机器贴图（`overlay_front_active`、`overlay_side_active` 等）是逐帧动画。这些 sprite 会全部进 2048×2048 的 `blocks` 图集，每个 tick 有上百个需要重传。

**为什么每次上传都这么慢？** 因为 `mipmapLevels > 0`。图集带 mip 链时，Intel 核显驱动每次 `glTexSubImage2D` 都要在 CPU 侧同步等待 GPU 并重建 mip 链。本机是 Iris Xe + **2022-01-27 的 30.0.101.1368 驱动**，这条慢路径特别贵。

### 5.4 A/B 实测

同一台机器、同一个 jar、同一场景（标题界面），唯一变量是 `mipmapLevels`，各采样渲染线程 8 次：

| `mipmapLevels` | 采样结果 |
|---|---|
| **4**（默认） | **8/8 全部阻塞在 `nglTexSubImage2D`** |
| **0** | 8/8 落在 `glfwSwapBuffers` / `glUniform3fv` / `glBufferData` / `Level.tickClient` 等正常路径，游戏可正常创建并进入世界 |

改成 0 之后，游戏不止能进标题界面，还正常加载了存档、跑完了 JEI 配方索引（`Starting JEI took 13.26 s`），渲染线程的采样全部落在帧提交路径上。

### 5.5 修复：`tuneClient` 任务

`build.gradle` 里注册了 `tuneClient`，并让 `runClient` 依赖它（`--dry-run` 可确认顺序：`:tuneClient` → `:runClient`）：

```bash
./gradlew tuneClient      # 手动跑也行
./gradlew runClient       # 会自动先跑 tuneClient
```

它把 `run/client/options.txt` 里三项拉到安全值：

| 键 | 值 | 原因 |
|---|---|---|
| `mipmapLevels` | `0` | 消除每次上传后的 mip 链重建，**这是决定性的一项** |
| `enableVsync` | `false` | vsync 会把每次上传的等待放大成整帧等待 |
| `maxFps` | `60` | 上限，避免无谓满载 |

输出示例：

```
> Task :tuneClient
[tuneClient] mipmapLevels 4 -> 0
```

游戏每次退出会重写 `options.txt`，所以必须在 `runClient` 之前再压一次，这就是让它 `dependsOn` 的原因。

### 5.6 代价与更根本的解法

**`mipmapLevels=0` 是有代价的**：远处方块会轻微闪烁（丢失 mipmap 抗锯齿）。对开发验证够用，但不是一个适合发布的画质设置。

根本解法是**升级 Intel 驱动**。本机停留在 2022-01 的 `30.0.101.1368`，而 Intel 后来把 Iris Xe 并入 *Intel Arc & Iris Xe Graphics* 统一驱动线（31.x/32.x）。升级后可以试着把 `mipmapLevels` 调回 `4` 复测——如果不再卡，把 `tuneClient` 里的值改成 `4` 或直接移除该项即可。

另一条路是装 **Sodium**（NeoForge 1.21.1 有对应版本）。它用 PBO 做异步纹理上传，能绕开这条同步慢路径。但会改变整个渲染路径，且与 Flywheel 的兼容性需要单独验证，所以没有默认接入——要试的话放进 `run/client/mods/`，不要写进 `build.gradle`。

### 5.7 JVM 参数

`build.gradle` 末尾给所有运行任务都加了：

```groovy
-Xms1G -Xmx4G -XX:+UseG1GC -XX:MaxGCPauseMillis=200
-XX:+ParallelRefProcEnabled -Dfile.encoding=UTF-8
```

**注意不要写在 `runs {}` 里**：moddev 2.0.120 的 `runs { configureEach { jvmArgument '...' } }` 是无效的——不报错，但实际启动命令里一个 `-Xmx` 都没有。`RunGameTask` 继承 `JavaExec`，直接配到任务上才生效：

```groovy
tasks.matching { it.name in ['runClient', 'runServer', 'runData'] }
        .configureEach { task -> if (task instanceof JavaExec) task.jvmArgs(gameJvmArgs) }
```

也没有加 `-XX:+AlwaysPreTouch`：它会在启动时把 4G 堆全部实体化，与常驻的 2G Gradle daemon 叠加，16G 机器上容易触发系统换页，启动期反而更卡，对稳定后的帧率也无收益。

**堆大小解决的是 OOM / GC 抖动，不解决帧率。** 帧率问题按 §5.2 的方法查渲染管线，别调堆。

---

## 6. 代理配置（`getsockopt` 超时必读）

报错：

```
Connection timed out: getsockopt. If you are behind an HTTP proxy,
please configure the proxy settings either in IDE or Gradle.
```

**根因**：Gradle 不读取 `HTTP_PROXY` / `HTTPS_PROXY` 环境变量，必须显式配置；
只用环境变量配了代理，Gradle 仍然会直连然后超时。

`gradle.properties` 尾部保留了这套配置的模板，**默认全部注释掉**（公开仓库里不留具体地址）。
需要时取消注释并改端口即可：

```properties
#systemProp.http.proxyHost=127.0.0.1
#systemProp.http.proxyPort=7890
#systemProp.https.proxyHost=127.0.0.1
#systemProp.https.proxyPort=7890
#systemProp.http.nonProxyHosts=localhost|127.0.0.1|[::1]|*.createmod.net|...
```

### 为什么必须按 host 分流

不是所有站点都需要代理，而**代理会截断大文件下载**：

```
Premature end of Content-Length delimited message body
(expected: 17,452,011; received: 10,452,676)
```

实测结论：

| 站点 | 直连 | 经代理 |
|---|---|---|
| `maven.neoforged.net` | ❌ 超时 | ✅ 200 |
| `mvn.devos.one` | ❌ TLS 握手超时 | ✅ 200（需带 Gradle UA，否则 403） |
| `maven.createmod.net`（Create/Ponder/Flywheel） | ✅ 快 | ⚠️ 大文件被截断 |
| `maven.gtceu.com`、`maven.firstdarkdev.xyz`、`repo.repsy.io`、`maven.blamejared.com`、Mojang | ✅ 快 | 不需要 |

所以 `nonProxyHosts` 要把这些直连站点列进去，只让 neoforged / devos 走代理。

### 换端口 / 全局生效

- 换端口：改 `gradle.properties` 里的 `proxyPort` 两处。
- 全局（IDE 里也生效）：把同一段 `systemProp.*` 写进 `~/.gradle/gradle.properties`。
- 查 Clash 实际端口：`netstat -ano | findstr LISTENING | findstr :789`

---
## 7. 从原仓库迁移代码时注意

原 `neo-1.21.1` 分支有几处"半成品状态"，直接照抄会踩坑：

| 原位置 | 问题 | 这里的处理 |
|---|---|---|
| `Greate.java:74-81` | `REGISTRATE.registerEventListeners(eventBus)`、`CREATIVE_TABS.register(eventBus)` 被注释；构造器 `modEventBus` 参数完全没用 | 恢复为标准写法：构造器注入 `IEventBus`，直接 `CREATIVE_TABS.register(modEventBus)` |
| 原代码用 `FMLJavaModLoadingContext.get()` | 该类在 NeoForge 1.21 已废弃 | 全部改为构造器注入 `IEventBus` / `ModContainer` |
| `GreateAddon.addRecipes()` | 方法体只剩注释 `//GreateRecipes.register(provider);` | 需要重新接回 GT 的 `IGTAddon` 配方入口 |
| `gradle.properties` | 属性名与主线不一致（`mod_version` vs `greate_version`） | 统一为 `mod_version` |
| 31 个 Mixin 直插 Create 内部 | 移植成本主要来源 | 见 `README.md`；建议先用官方扩展点，实在不行再 Mixin |

### 编写 `Tiered*BlockEntity` 时需要核对的接口位置

下列位置在原分支（Create 6.0.8-158）上是有效的，**升级到 6.0.10 后请对照源码复核，
不要直接照抄**：

| 接口 | 6.0.8 下的位置 | 说明 |
|---|---|---|
| `BlockStressValues.IMPACTS` / `CAPACITIES` | `com.simibubi.create.api.stress` | 应力注册，`GreateConfigs` 依赖它 |
| `CreateRegistrate.setCreativeTab` | `create.foundation.data` | 原版每个 registry 类开头都调用一次，之后注册的条目全部进该页 |
| `ConfigBase` / `FontHelper` | `net.createmod.catnip.config` / `.lang` | Catnip 已与 Create 分离为独立库 |
| `ItemDescription` / `TooltipModifier` | `create.foundation.item` | tooltip 拼装 |

具体签名以 `create-1.21.1-6.0.10-281` 的源码 jar 为准：
`./gradlew idea` 后 IDEA 会自动挂源码（工程已开 `downloadSources = true`）。

---

## 8. 许可

本工程骨架为原创代码（MIT）。引入的原项目代码为 MIT，需保留版权声明。
贴图授权见上级 `README.md` §7。
