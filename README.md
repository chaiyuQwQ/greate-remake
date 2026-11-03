<p align="center">
  <img src="src/main/resources/logo.png" alt="Greate Remake" width="200">
</p>

# Greate Remake

**Greg-ify your Create.** Ten tiers of Create machines built on GregTech CEu materials and voltages —
for Minecraft 1.21.1 / NeoForge.

![Minecraft](https://img.shields.io/badge/Minecraft-1.21.1-green)
![NeoForge](https://img.shields.io/badge/NeoForge-21.1.219-orange)
![Create](https://img.shields.io/badge/Create-6.0.10--281-yellow)
![GTCEu](https://img.shields.io/badge/GTCEu-7.0.2-blue)
![License](https://img.shields.io/badge/License-MIT-lightgrey)
![Status](https://img.shields.io/badge/status-0.1.0%20preview-red)

[modrinth](https://modrinth.com/mod/greate-remake)

[中文](#中文) · [English](#english)

> **非官方重制 / Unofficial remake.** 本项目是 [Greate](https://github.com/GreateBeyondTheHorizon/Greate)
> （MIT，作者 Electrolyte、lonevox）的 1.21.1 重制版，与原作者无隶属关系，不代表其立场。
> This is an unofficial remake of Greate, not affiliated with its authors.

---

## 中文

### 这是什么

把 Create 的动能体系（转速 RPM / 应力 SU）接到 GregTech CEu 的材料与电压等级上：
同样的传动轴、齿轮、磨石、压床、搅拌器、泵、粉碎轮、封装风扇、机械锯，
每种都有 **10 个材料等级（ULV→UHV）**，等级决定三件事 —— 它能承受多少应力、
它能执行几级的配方、它消耗多少应力。

### 特性

- **10 级动能方块**：传动轴、齿轮、大齿轮、齿轮箱（含竖直齿轮箱物品）、粉碎轮、磨石、
  封装风扇、机械搅拌器、机械压床、机械泵、机械锯 —— 共 130 个方块 / 125 个物品。
- **网络应力上限由最低等级决定**：整条网络的容量取「网络内最低等级方块」的值，
  超出上限的应力会让机器停转，想提产能就得换高等级的传动部件。
- **配方分级门禁**：`greate_remake:milling / pressing / mixing / crushing / sawing` 五种配方类型，
  机器等级必须 ≥ 配方等级，部分配方还要求指定电路。
- **GT 配方自动投影**：磨石与粉碎轮吃 GT 研磨机配方，压床吃压弯机 / 压缩机配方，
  锯吃 GT 切割机配方 —— 不需要为每个 GT 配方手写一遍。
- **风处理等级门禁**：鼓风 / 烟熏 / 洗涤 / 缠魂 各有最低风扇等级，另有一条材料轴
  （被处理物品的材料等级不能高于风扇等级）。Create 原生风扇不受限制。
- **传送带分级**：5 种聚合物 × 2 个等级，带长上限按聚合物配置。
- **配套体验**：JEI 五个配方分类、Ponder 教程（含两条本 mod 专属场景）、7 种语言。

### 环境要求

| 项目 | 版本 |
| --- | --- |
| Minecraft | 1.21.1 |
| NeoForge | 21.1.219 或更高 |
| Create | 6.0.10-281 或更高（6.0.x） |
| GregTech CEu: Modern | 7.0.2 |

只面向 1.21.1 + GTCEu 7.0.2 这一条版本线；换 GT 大版本会有 API 差异，未做兼容。

### 安装

1. 装好 NeoForge 21.1.219+、Create 6.0.10+、GTCEu 7.0.2；
2. 把 `greate_remake-<version>.jar` 丢进 `mods/`；
3. 没有额外前置依赖（本 mod 是 slim jar，不内嵌任何库）。

### 等级体系

| Tier | 0 | 1 | 2 | 3 | 4 | 5 | 6 | 7 | 8 | 9 |
| --- | --- | --- | --- | --- | --- | --- | --- | --- | --- | --- |
| GT 电压 | ULV | LV | MV | HV | EV | IV | LuV | ZPM | UV | UHV |
| 应力档位 | ULS | LS | MS | HS | ES | IS | LuS | ZPMS | US | UHS |
| 网络上限 (SU) | 8 | 32 | 128 | 512 | 2048 | 8192 | 32768 | 131072 | 524288 | 2097152 |
| 材质 | 安山合金 | 钢 | 铝 | 不锈钢 | 钛 | 钨钢 | 镀铑钯 | 硅岩合金 | 𫟼 | 中子素 |

网络上限 = `8 × 4^tier`，全部可在配置里逐个改。

### 机器一览

| 机器 | 说明 |
| --- | --- |
| 传动轴 / 齿轮 / 大齿轮 / 齿轮箱 | 基础传动件，等级决定网络容量与应力消耗 |
| 竖直齿轮箱 | 同一个齿轮箱方块 + 第二个物品，用于换向 |
| 磨石 | 磨粉，吃 GT 研磨机配方，带概率加成 |
| 机械压床 | 压板 / 压制，吃 GT 压弯机与压缩机配方 |
| 机械搅拌器 | 混合，包含合金与钢的合成链 |
| 机械泵 | 流体传输 |
| 粉碎轮 | 粉碎，吃 GT 研磨机配方 |
| 封装风扇 | 鼓风 / 烟熏 / 洗涤 / 缠魂，带等级门禁 |
| 机械锯 | 切割，吃 GT 切割机配方 |
| 传送带 + 连接器 | 5 种聚合物 × 2 个等级，连接器同等级才能成链 |

### 配置

服务端配置 `config/greate_remake-server.toml`：

| 段 | 作用 |
| --- | --- |
| `max_capacity.<材质>` | 每个等级的应力上限（默认 `8 × 4^tier`） |
| `machine_impact.<机器>` | 各机器 tier-0 的应力消耗基准，实际值 = 基准 × (tier + 1) |
| `belt_length.<聚合物>` | 每种传送带的长度上限（默认 20，最小 5） |
| `fan.tier_gate` / `fan.min_tier_*` | 风处理等级门禁总开关与各类型最低等级（默认 0/0/1/2） |
| `recipes.hard_create_recipes` | 开启后移除 Create 被本 mod 取代的原版配方（默认关，避免破坏 Create 进度） |

### 已知限制

- 缠魂（haunting）没有分级版本，只有风扇等级门禁。
- 电线涂层（spout 涂层）配方尚未移植。
- 暂不提供 KubeJS 集成。
- Java 包名沿用 `electrolyte.greate`，因此**不能与原版 Greate 装在一起**（两者的支持版本也不重叠）。
- 0.1.0 属于开发预览：开发期 GameTest 32/32 与 CI 全绿（测试代码与测试结构不随仓库发布），
  但未做整合包级的长时间压力测试。

### 致谢与许可

- 上游 [Greate](https://github.com/GreateBeyondTheHorizon/Greate)（MIT，© 2024 GreateBeyondTheHorizon）：
  本项目的贴图直接来自上游，功能设计与类结构也参考了它。
- [Create](https://github.com/Creators-of-Create/Create) 与 [Ponder](https://github.com/Creators-of-Create/Ponder)（MIT）、
  [GregTech CEu: Modern](https://github.com/GregTechCEu/GregTech-Modern)（LGPL-3.0）、
  [Registrate](https://github.com/tterrag1098/Registrate)（MPL-2.0）。
- 本项目以 **MIT** 发布，见 [LICENSE](LICENSE)；完整第三方声明见 [THIRD-PARTY.md](THIRD-PARTY.md)。

### 开发

```bash
./gradlew build        # 产出 build/libs/greate_remake-<version>.jar
./gradlew runClient    # 开发客户端
./gradlew runData      # 重新生成 datagen 产物（模型 / 方块状态 / 语言）
```

环境要求、目录约定、性能问题与排障细节见 **[BUILDING.md](BUILDING.md)**。

---

## English

### What this is

It wires Create's kinetic system (RPM / stress) into GregTech CEu's materials and voltage tiers.
Shafts, cogwheels, millstones, presses, mixers, pumps, crushing wheels, encased fans and mechanical
saws all come in **ten material tiers (ULV → UHV)**. The tier decides three things: how much stress
the block can take, which recipe tiers it can run, and how much stress it consumes.

### Features

- **Ten tiers of kinetic blocks** — shafts, cogwheels, large cogwheels, gearboxes (plus a vertical
  gearbox item), crushing wheels, millstones, encased fans, mechanical mixers, presses, pumps and saws:
  130 blocks / 125 items in total.
- **Network capacity is capped by the weakest block** — a kinetic network can only carry the stress
  its lowest-tier component supports. Overload it and machines stall, so upgrades mean replacing the
  drivetrain, not just the machine.
- **Tier-gated recipes** — five recipe types (`greate_remake:milling / pressing / mixing / crushing /
  sawing`). The machine tier must be at least the recipe tier; some recipes also require a circuit.
- **GT recipes are projected automatically** — millstones and crushing wheels run GT macerator
  recipes, presses run bender/compressor recipes, saws run cutter recipes. No per-recipe busywork.
- **Fan processing gate** — blasting / smoking / splashing / haunting each require a minimum fan tier,
  plus a material axis (the processed item's material tier may not exceed the fan tier). Vanilla
  Create fans are unaffected.
- **Tiered belts** — five polymers, each covering two tiers, with a configurable maximum length.
- **Quality of life** — five JEI categories, Ponder tutorials (including two scenes written for this
  mod), and seven languages.

### Requirements

| Component | Version |
| --- | --- |
| Minecraft | 1.21.1 |
| NeoForge | 21.1.219+ |
| Create | 6.0.10-281+ (6.0.x) |
| GregTech CEu: Modern | 7.0.2 |

Only the 1.21.1 / GTCEu 7.0.2 line is supported; other GT major versions have API differences.

### Installation

Install NeoForge 21.1.219+, Create 6.0.10+ and GTCEu 7.0.2, then drop
`greate_remake-<version>.jar` into `mods/`. No extra dependencies — the jar is slim and bundles
no libraries.

### Tiers

| Tier | 0 | 1 | 2 | 3 | 4 | 5 | 6 | 7 | 8 | 9 |
| --- | --- | --- | --- | --- | --- | --- | --- | --- | --- | --- |
| GT voltage | ULV | LV | MV | HV | EV | IV | LuV | ZPM | UV | UHV |
| Stress class | ULS | LS | MS | HS | ES | IS | LuS | ZPMS | US | UHS |
| Network cap (SU) | 8 | 32 | 128 | 512 | 2048 | 8192 | 32768 | 131072 | 524288 | 2097152 |
| Material | Andesite Alloy | Steel | Aluminium | Stainless Steel | Titanium | Tungsten Steel | Rhodium-Plated Palladium | Naquadah Alloy | Darmstadtium | Neutronium |

The cap is `8 × 4^tier` and every value is configurable.

### Configuration

Server config `config/greate_remake-server.toml`:

| Section | Purpose |
| --- | --- |
| `max_capacity.<material>` | Stress cap per tier (default `8 × 4^tier`) |
| `machine_impact.<machine>` | Tier-0 stress impact per machine; actual value = base × (tier + 1) |
| `belt_length.<polymer>` | Maximum belt length per polymer (default 20, minimum 5) |
| `fan.tier_gate`, `fan.min_tier_*` | Fan tier gate master switch and per-type minimum tiers (0/0/1/2) |
| `recipes.hard_create_recipes` | Remove the Create recipes this mod replaces (off by default) |

### Known limitations

- No tiered haunting type yet — only the fan tier gate applies.
- Spout wire-coating recipes are not ported.
- No KubeJS integration.
- The Java package is still `electrolyte.greate`, so this mod **cannot be installed alongside the
  original Greate** (their supported versions do not overlap either).
- 0.1.0 is a development preview: GameTest passed 32/32 and CI was green during development
  (the test suite and its structure are not shipped in this repository), but no long-running
  modpack stress test has been done.

### Credits and license

- Upstream [Greate](https://github.com/GreateBeyondTheHorizon/Greate) (MIT, © 2024
  GreateBeyondTheHorizon): the textures come from there, and its design shaped this remake.
- [Create](https://github.com/Creators-of-Create/Create) and
  [Ponder](https://github.com/Creators-of-Create/Ponder) (MIT),
  [GregTech CEu: Modern](https://github.com/GregTechCEu/GregTech-Modern) (LGPL-3.0),
  [Registrate](https://github.com/tterrag1098/Registrate) (MPL-2.0).
- Released under **MIT** — see [LICENSE](LICENSE) and [THIRD-PARTY.md](THIRD-PARTY.md).

### Building

```bash
./gradlew build        # build/libs/greate_remake-<version>.jar
./gradlew runClient    # dev client
./gradlew runData      # regenerate datagen output (models, blockstates, lang)
```

See **[BUILDING.md](BUILDING.md)** for the environment, layout, performance notes and
troubleshooting (Chinese).

---

## 文档索引 / Documentation

| 文档 | 内容 |
| --- | --- |
| [BUILDING.md](BUILDING.md) | 构建环境、目录约定、性能问题、排障 FAQ |
| [CHANGELOG.md](CHANGELOG.md) | 版本变更记录 |
| [THIRD-PARTY.md](THIRD-PARTY.md) | 第三方许可与派生内容声明 |
| [LICENSE](LICENSE) · [LICENSES/](LICENSES/) | 本项目 MIT + 上游 MIT 原文 |
