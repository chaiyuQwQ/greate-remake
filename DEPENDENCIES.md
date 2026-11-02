# 构建 / 运行依赖清单

**目标环境**：Minecraft 1.21.1 · NeoForge 21.1.219 · Create 6.0.10-281 · GTCEu Modern 7.0.2

本清单只使用**上游官方仓库与官方坐标**，没有镜像、没有降级、没有版本妥协。
每一行的版本都注明了依据，可以逐条复核。

---

## 0. 版本依据（先看这个，再决定要不要改）

三个模组包自带的元数据是唯一可信来源，我直接读了：

**① `create-1.21.1-6.0.10-281.jar` 的 `META-INF/jarjar/metadata.json`** —— Create 自己打包内嵌的库：

| 内嵌制品 | 版本 |
|---|---|
| `net.createmod.ponder:ponder-neoforge` | `1.0.82+mc1.21.1` |
| `dev.engine-room.flywheel:flywheel-neoforge-1.21.1` | `1.0.6` |
| `com.tterrag.registrate:Registrate` | `MC1.21-1.3.0+67` |

**② `gtceu-1.21.1-7.0.2.jar` 的 `META-INF/jarjar/metadata.json`**：

| 内嵌制品 | 版本 |
|---|---|
| `com.lowdragmc.ldlib:ldlib-neoforge-1.21.1` | `1.0.35.a` |
| `dev.toma.configuration:configuration-1.21.1` | `3.1.0-neoforge` |
| `com.tterrag.registrate:Registrate` | `MC1.21-1.3.0+67` |

**③ `neoforge.mods.toml` 的 required 声明**：

- Create：`neoforge [21.1.219,)`、`minecraft [1.21.1]`、`flywheel [1.0.0,2.0)`、`ponder [1.0.82,)`
- GTCEu：`neoforge [21.1,)`、`minecraft [1.21, 1.21.1]`、`ldlib [1.0.35.a,)`、`configuration [3.1.0,)`

> **所以 ponder 用 `1.0.82+mc1.21.1`、flywheel 用 `1.0.6` 不是"挑了旧版"，
> 而是 Create 6.0.10 出厂就带这两个版本**。用更新的 1.0.87 / 1.0.7 虽然落在声明区间内，
> 但等于在测一个 Create 没测过的组合。

---

## A. 平台与工具链

| # | 制品 | 坐标 / URL | 版本 | 仓库 | 用途 | 必需性 |
|---|---|---|---|---|---|---|
| A1 | Gradle 发行版 | `gradle-8.14.3-bin.zip` | 8.14.3 | `services.gradle.org/distributions/` | Wrapper 引导 | **必需** |
| A2 | moddev 插件 | `net.neoforged.moddev` | `2.0.120` | Gradle Plugin Portal | 提供 `neoForge{}` 扩展、`runClient/runData` 任务 | **必需** |
| A3 | NeoForge | `net.neoforged:neoforge` | `21.1.219` | `maven.neoforged.net/releases` | 加载器 + userdev 制品 | **必需** |
| A4 | NeoForm Runtime | `net.neoforged:neoform-runtime` | `1.0.43` | `maven.neoforged.net/releases` | 反编译/合并 Minecraft，**最容易卡住的一步** | **必需** |
| A5 | NeoForm 配置 | `net.neoforged:neoform:1.21.1-<ts>` | 由 A2 决定 | `maven.neoforged.net/releases` | MC 补丁与合并脚本 | **必需**（版本勿手填） |
| A6 | Minecraft 本体 + 库 | `client.jar` / `server.jar` / `libraries` | 1.21.1 | `piston-meta.mojang.com`、`piston-data.mojang.com`、`libraries.minecraft.net` | 游戏本体与依赖库 | **必需** |
| A7 | Minecraft 资源 | asset index + objects | 1.21.1 | `resources.download.minecraft.net` | 仅 `runClient` 需要（音效、语言、原版贴图） | 运行客户端时必需 |
| A8 | Parchment 映射 | `org.parchmentmc.data:parchment-1.21.1:2024.11.17@zip` | 2024.11.17 | `maven.parchmentmc.org` | 参数名映射，提升源码可读性 | 建议 |
| A9 | Librarian | `org.parchmentmc:librarian` | 由插件决定 | `maven.parchmentmc.org` | A8 的加载器 | 建议 |

> **A8 有个坑**：Parchment 按 MC 版本分坐标。`parchment-1.21.1` 的发布是
> `2024.11.13` / `2024.11.17`；`2024.11.10` 只存在于 `parchment-1.21` 下。
> 原 Greate 分支写的是 `minecraft=1.21` + `2024.11.10`（因为当时 1.21.1 还没有独立发布）。
> 本工程已改为 `1.21.1` + `2024.11.17`（该坐标实测存在）。

---

## B. 模组依赖（编译 + 运行）

| # | 制品 | 坐标 | 版本 | 仓库 | 用途 | 编译 | 运行 |
|---|---|---|---|---|---|---|---|
| B1 | Create | `com.simibubi.create:create-1.21.1:6.0.10-281:slim` | 6.0.10-281 | `maven.createmod.net` | 动能体系的基类、Registrate 封装、Ponder | ✅ | ✅ |
| B2 | Ponder | `net.createmod.ponder:ponder-neoforge` | `1.0.82+mc1.21.1` | `maven.createmod.net` | Create 硬依赖（`[1.0.82,)`），教程场景 | ✅ | ✅ |
| B3 | Flywheel API | `dev.engine-room.flywheel:flywheel-neoforge-api-1.21.1` | `1.0.6` | `maven.createmod.net` | 实例化渲染 API，编译期 | ✅ | — |
| B4 | Flywheel | `dev.engine-room.flywheel:flywheel-neoforge-1.21.1` | `1.0.6` | `maven.createmod.net` | Create 硬依赖，运行时渲染后端 | — | ✅ |
| B5 | Registrate | `com.tterrag.registrate:Registrate` | `MC1.21-1.3.0+67` | `mvn.devos.one/snapshots` | `CreateRegistrate` / `GTRegistrate` 的底层，注册链全靠它 | ✅ | ✅ |
| B6 | GTCEu Modern | `com.gregtechceu.gtceu:gtceu-1.21.1` | `7.0.2` | `maven.gtceu.com` | 材料体系、`TagPrefix`、电压等级、`GTRegistrate` | ✅ | ✅ |
| B7 | LDLib | `com.lowdragmc.ldlib:ldlib-neoforge-1.21.1` | `1.0.35.a` | `maven.firstdarkdev.xyz/snapshots` | GT 的 UI/渲染库（GT 硬依赖） | ✅ | ✅ |
| B8 | Configuration | `dev.toma.configuration:configuration-1.21.1` | `3.1.1-neoforge` | `repo.repsy.io/mvn/toma/public` | GT 的配置框架；**重制版用它写配置**（`Configuration.registerConfig`） | ✅ | ⚠️ 见 §B-注 |
| B9 | Kotlin stdlib | `org.jetbrains.kotlin:kotlin-stdlib` | `2.0.21` | `mavenCentral()` | GT 用 Kotlin 写，运行时必需 | ✅ | ✅ |
| B10 | MixinExtras | `io.github.llamalad7:mixinextras-neoforge` | `0.5.0-rc.3` | `mavenCentral()` | `@WrapOperation` 等 Mixin 增强，替代部分 `@Redirect` | ✅ | ✅ |
| B11 | MixinExtras 注解处理器 | `io.github.llamalad7:mixinextras-common` | `0.5.0-rc.3` | `mavenCentral()` | 编译期注解处理（`compileOnly` + `annotationProcessor`） | ✅ | — |
| B12 | JEI 本体 | `mezz.jei:jei-1.21.1-neoforge` | `19.21.0.247` | `maven.blamejared.com` | 配方查看器，本地运行时用 | — | 可选 |
| B13 | JEI API | `mezz.jei:jei-1.21.1-common-api` / `jei-1.21.1-neoforge-api` | `19.21.0.247` | `maven.blamejared.com` | 写 JEI 分类时的编译期 API | ✅ | — |

### §B-注：B8 的版本为什么是 3.1.1 而不是 3.1.0

- GT 7.0.2 的 POM 与 `mods.toml` 都写 `3.1.0`。
- 但 `repo.repsy.io` 上 `configuration-1.21.1` **只有 3.1.1 系列**（`3.1.1-common/fabric/forge/neoforge`），
  `3.1.0-neoforge` 的 `.pom` 实测 404。
- **运行时不受影响**：`gtceu-1.21.1-7.0.2.jar` 已经通过 jarJar 内嵌了
  `configuration-1.21.1-3.1.0-neoforge.jar`，游戏加载时用内嵌那份。
- 编译期需要它是因为 `Greate.java` 原代码 `import dev.toma.configuration.Configuration`，
  所以类路径上必须有这个 jar。用 3.1.1（同 minor 的补丁版，API 未变）即可。
- 若你坚持要 3.1.0：`gtceu-7.0.2.jar!/META-INF/jarjar/configuration-1.21.1-3.1.0-neoforge.jar`
  就是原制品，解出来当本地文件依赖即可（见 §E）。

---

## C. 明确**不需要**的（避免多下）

| 制品 | 为什么不需要 |
|---|---|
| `dev.engine-room.vanillin` | Create 的 POM 里有，但 `mods.toml` 未声明 required，`transitive=false` 时不会拉取 |
| `top.theillusivec4.curios` | 同上，Create 的可选饰品集成 |
| `dev.ftb.mods:ftb-chunks/teams/library` | Create 开发环境用的 runtime 依赖，非运行必需 |
| `maven.modrinth:journeymap` / `farmers-delight`、`cc.tweaked` | Create 的可选兼容项（`type = "optional"`），装了才有用 |
| `dev.architectury` / `dev.latvian.mods:rhino` / `kubejs` | 原 Greate 支持 KubeJS 脚本扩展；重制版不做 KubeJS 兼容就不需要 |
| `dev.emi:emi` | 原分支里被注释掉了（`//compileOnly "dev.emi:emi-..."`） |

> 这些之所以出现在 Create 的 POM 里，是因为 POM 用的是 Create 作者自己的开发 runtime 配置；
> 判定"运行必需"要看 `neoforge.mods.toml` 里的 `type = "required"`，那里只有 flywheel 和 ponder。

---

## D. 资源清单

| 资源 | 位置 | 说明 |
|---|---|---|
| 模型 / 方块状态 / lang | `../export/assets/greate/` | 1168 个文件，原样可用，见上级 README §3 |
| 贴图 | `../export/assets/greate/textures/` | 239 张，放 `src/main/resources`（不要放 generated） |
| 物品栏图标 | `../export/icons/` | 125 张 PNG，仅作参考，不进构建 |
| MC 原版资源 | A7 自动下载 | `runClient` 首次启动会拉 assets，约 300–500 MB |
| 无其他外部资源 | — | 音效、粒子等本项目未使用 |

---

## E. 如果某个制品取不到：等价替代（不是降级）

这些替代品都是**同一个二进制**，只是换了个交付方式：

| 取不到的制品 | 等价来源 |
|---|---|
| `Registrate:MC1.21-1.3.0+67`（devos.one 偶尔 TLS 超时） | `create-1.21.1-6.0.10-281.jar!/META-INF/jarjar/Registrate-MC1.21-1.3.0+67.jar` 或 `gtceu-7.0.2.jar!/META-INF/jarjar/Registrate-MC1.21-1.3.0+67.jar` |
| `ponder-neoforge:1.0.82+mc1.21.1` | `create...jar!/META-INF/jarjar/ponder-neoforge-1.0.82+mc1.21.1.jar` |
| `flywheel-neoforge-1.21.1:1.0.6` | `create...jar!/META-INF/jarjar/flywheel-neoforge-1.21.1-1.0.6.jar` |
| `ldlib 1.0.35.a`（firstdarkdev 偶发 TLS 握手失败） | `gtceu-7.0.2.jar!/META-INF/jarjar/ldlib-neoforge-1.21.1-1.0.35.a.jar` |
| `configuration-1.21.1:3.1.0-neoforge` | `gtceu-7.0.2.jar!/META-INF/jarjar/configuration-1.21.1-3.1.0-neoforge.jar` |

提取方式：

```bash
unzip -j create-1.21.1-6.0.10-281.jar 'META-INF/jarjar/*.jar' -d libs/
```

**只有在确实取不到时才用**。默认 `build.gradle` 仍然只配置官方仓库，没有加任何本地兜底。
若你决定用本地文件，把这几个 jar 放进 `dev-env/libs/`，然后在 `build.gradle` 的
`dependencies {}` 里追加（自行决定要不要加，我没有预设）：

```groovy
// 可选兜底：仅当对应 Maven 不可达时启用
// dependencies {
//     implementation files('libs/Registrate-MC1.21-1.3.0+67.jar')
//     implementation files('libs/ponder-neoforge-1.0.82+mc1.21.1.jar')
//     implementation files('libs/flywheel-neoforge-1.21.1-1.0.6.jar')
//     implementation files('libs/ldlib-neoforge-1.21.1-1.0.35.a.jar')
//     implementation files('libs/configuration-1.21.1-3.1.0-neoforge.jar')
// }
```

---

## F. 开工前自检

```bash
python verify-deps.py          # 逐条 HEAD 请求，打印 OK / FAIL
```

脚本会把上面 A、B 两节里所有可直连的 URL 打一遍，输出表格。
**FAIL 的那几行就是需要你手工提供的**，或者说明该站点在你的网络下需要特殊处理。

通过后再：

```bash
export JAVA_HOME=/path/to/jdk-21
./gradlew dependencies --configuration compileClasspath   # 确认依赖树完整
./gradlew build
```

---

## G. 已知的语义风险（与网络无关）

1. **`neo_version=21.1.219` 是 Create 声明的下限**，不是它一定测试过的版本。
   如果你想跟 Create 官方整合包对齐，可以升到最新的 21.1.x（它声明 `[21.1.219,)`，向上兼容）。
2. **Create 用 `:slim` 变体**（不含 jarJar 内嵌库），所以 B2–B5 必须显式声明，
   漏一个就是 `NoClassDefFoundError`。这也是为什么版本要和内嵌清单严格对齐。
3. **`minecraft_version_range=[1.21.1]`**：Create 与 GT 都只声明 1.21.1，不要放宽。

---

## H. 关于你提供的两个文件

### H1. `neoform-runtime-1.0.43-all.jar` —— **正好是缺的那个，可用**

这是 `net.neoforged:neoform-runtime:1.0.43` 的 fat jar（`Main-Class: net.neoforged.neoform.runtime.cli.Main`，
1626 个条目，自带全部依赖）。它就是本机构建卡住的 `:neoFormRuntimeTool` 配置所需的制品。

但**不能只丢一个 jar**，必须同时提供元数据，否则会报：

```
Unable to find a variant with the requested capability:
coordinates 'net.neoforged:neoform-runtime-external-tools'
```

原因是 moddev 按 **capability** 找它，而 capability 只存在于 Gradle 模块元数据里。
模板已放在 `local-staging/neoform-runtime-1.0.43/`（POM + 合成 `.module`），
布局与注入方式见 `local-staging/README.md`、机制说明见 `OFFLINE-STAGING.md` §4。

### H2. `neoforge-21.1.219-installer.jar` —— **Gradle 构建用不上**

它是**安装器**，不是开发制品。内部只有：

```
install_profile.json      ← 安装流程描述
version.json              ← 启动参数（含 --fml.neoFormVersion 20240808.144430）
data/client.lzma          ← 客户端补丁
data/server.lzma          ← 服务端补丁
com/google/gson/**        ← 安装器自己的依赖
```

没有 `userdev`，也没有 Maven 布局，所以 Gradle 取不到任何东西。
唯一有用的是 `version.json` 里的 NeoForm 版本号，可用来确认：

```
net.neoforged:neoform:1.21.1-20240808.144430
```

**Gradle 实际需要的是这 4 个文件**（`21.1.219` 为例）：

| 文件 | 必需 | 用途 |
|---|---|---|
| `neoforge-21.1.219-userdev.jar` | ✅ | 反编译配置、AT、补丁清单，编译/运行的核心 |
| `neoforge-21.1.219-universal.jar` | ✅ | 运行期模组本体 |
| `neoforge-21.1.219.pom` | ✅ | 依赖声明 |
| `neoforge-21.1.219.module` | ✅ | 变体与 capability |
| `neoforge-21.1.219-sources.jar` | 可选 | IDE 看源码 |

下载地址：

```
https://maven.neoforged.net/releases/net/neoforged/neoforge/21.1.219/neoforge-21.1.219-userdev.jar
https://maven.neoforged.net/releases/net/neoforged/neoforge/21.1.219/neoforge-21.1.219-universal.jar
https://maven.neoforged.net/releases/net/neoforged/neoforge/21.1.219/neoforge-21.1.219.pom
https://maven.neoforged.net/releases/net/neoforged/neoforge/21.1.219/neoforge-21.1.219.module
```

放进 `local-staging/net/neoforged/neoforge/21.1.219/` 即可（保持文件名不变）。

### H3. 验证结果：✅ 已完整跑通

配置代理后，用**正式制品**（未用任何本地替代）完成了完整验证：

| 阶段 | 结果 |
|---|---|
| Gradle 脚本解析 | ✅ |
| Create 6.0.10-281 / GTCEu 7.0.2 / Ponder / Flywheel / Registrate / LDLib / Configuration / JEI 解析 | ✅ |
| `neoform-runtime:1.0.43` + 五个外部工具 | ✅ |
| `:createMinecraftArtifacts`（NeoForm 反编译） | ✅ 151s |
| `:compileJava` / `:jar`（`./gradlew build`） | ✅ 2m45s，产出 `build/libs/greate-0.1.0.jar` |
| `:runData`（游戏内数据生成） | ✅ 42s，NeoForge 21.1.219 + Create + GTCEu 同时加载无冲突 |

日志证据：

```
--fml.neoForgeVersion, 21.1.219
--fml.neoFormVersion, 20240808.144430
[modloading-worker-0/INFO] [el.gr.Greate/]: [Greate] remake skeleton loaded (Create 6.0.10 / GTCEu 7.0.2)
[modloading-sync-worker/INFO] [GTCEu/]: Registering GTCEu Materials
BUILD SUCCESSFUL
```

**所以 `local-staging/` 不是必需的**，它只是给"完全断网"场景留的后手。
