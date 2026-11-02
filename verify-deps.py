#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""
构建依赖可达性自检。

逐个 HEAD 请求 → 打印 OK / FAIL。
FAIL 的行就是需要你本地手工提供（或用 DEPENDENCIES.md §E 的等价来源）的制品。

用法：
    python verify-deps.py            # 默认 10 秒超时
    python verify-deps.py --timeout 30
"""

import argparse
import socket
import sys
import urllib.request
from concurrent.futures import ThreadPoolExecutor

MC = "1.21.1"
NEO = "21.1.219"
CREATE = "6.0.10-281"
GT = "7.0.2"

# (分组, 名称, URL, 说明)
ENTRIES = [
    # ---- A. 平台与工具链 ----
    ("A", "Gradle 发行版 8.14.3",
     "https://services.gradle.org/distributions/gradle-8.14.3-bin.zip", "wrapper 引导"),
    ("A", "moddev 插件标记 2.0.120",
     "https://plugins.gradle.org/m2/net/neoforged/moddev/net.neoforged.moddev.gradle.plugin/2.0.120/net.neoforged.moddev.gradle.plugin-2.0.120.pom",
     "neoForge{} 扩展"),
    ("A", f"NeoForge {NEO}",
     f"https://maven.neoforged.net/releases/net/neoforged/neoforge/{NEO}/neoforge-{NEO}.pom", "加载器"),
    ("A", "NeoForm Runtime 1.0.43",
     "https://maven.neoforged.net/releases/net/neoforged/neoform-runtime/1.0.43/neoform-runtime-1.0.43.pom",
     "反编译/合并 MC"),
    ("A", "Mojang 版本清单",
     "https://piston-meta.mojang.com/mc/game/version_manifest_v2.json", "定位 1.21.1 各制品"),
    ("A", "Parchment 1.21.1:2024.11.17",
     "https://maven.parchmentmc.org/org/parchmentmc/data/parchment-1.21.1/2024.11.17/parchment-1.21.1-2024.11.17.zip",
     "参数名映射"),

    # ---- B. 模组依赖 ----
    ("B", f"Create {CREATE} (slim)",
     f"https://maven.createmod.net/com/simibubi/create/create-{MC}/{CREATE}/create-{MC}-{CREATE}-slim.jar",
     "主依赖（不含内嵌库）"),
    ("B", "Ponder 1.0.82+mc1.21.1",
     "https://maven.createmod.net/net/createmod/ponder/ponder-neoforge/1.0.82+mc1.21.1/ponder-neoforge-1.0.82+mc1.21.1.pom",
     "Create 硬依赖"),
    ("B", "Flywheel API 1.0.6",
     f"https://maven.createmod.net/dev/engine-room/flywheel/flywheel-neoforge-api-{MC}/1.0.6/flywheel-neoforge-api-{MC}-1.0.6.pom",
     "编译期渲染 API"),
    ("B", "Flywheel 1.0.6",
     f"https://maven.createmod.net/dev/engine-room/flywheel/flywheel-neoforge-{MC}/1.0.6/flywheel-neoforge-{MC}-1.0.6.pom",
     "运行时渲染后端"),
    ("B", "Registrate MC1.21-1.3.0+67",
     "https://mvn.devos.one/snapshots/com/tterrag/registrate/Registrate/MC1.21-1.3.0+67/Registrate-MC1.21-1.3.0+67.pom",
     "注册链底层"),
    ("B", f"GTCEu {GT}",
     f"https://maven.gtceu.com/com/gregtechceu/gtceu/gtceu-{MC}/{GT}/gtceu-{MC}-{GT}.pom", "主依赖"),
    ("B", "LDLib 1.0.35.a",
     f"https://maven.firstdarkdev.xyz/snapshots/com/lowdragmc/ldlib/ldlib-neoforge-{MC}/1.0.35.a/ldlib-neoforge-{MC}-1.0.35.a.pom",
     "GT 的 UI 库"),
    ("B", "Configuration 3.1.1-neoforge",
     f"https://repo.repsy.io/mvn/toma/public/dev/toma/configuration/configuration-{MC}/3.1.1-neoforge/configuration-{MC}-3.1.1-neoforge.pom",
     "GT 配置框架"),
    ("B", "Kotlin stdlib 2.0.21",
     "https://repo1.maven.org/maven2/org/jetbrains/kotlin/kotlin-stdlib/2.0.21/kotlin-stdlib-2.0.21.pom",
     "GT 运行时依赖"),
    ("B", "MixinExtras neoforge 0.5.0-rc.3",
     "https://repo1.maven.org/maven2/io/github/llamalad7/mixinextras-neoforge/0.5.0-rc.3/mixinextras-neoforge-0.5.0-rc.3.pom",
     "Mixin 增强"),
    ("B", "MixinExtras common 0.5.0-rc.3",
     "https://repo1.maven.org/maven2/io/github/llamalad7/mixinextras-common/0.5.0-rc.3/mixinextras-common-0.5.0-rc.3.pom",
     "注解处理器"),
    ("B", "JEI neoforge 19.21.0.247",
     f"https://maven.blamejared.com/mezz/jei/jei-{MC}-neoforge/19.21.0.247/jei-{MC}-neoforge-19.21.0.247.pom",
     "配方查看器（可选）"),
    ("B", "JEI common-api 19.21.0.247",
     f"https://maven.blamejared.com/mezz/jei/jei-{MC}-common-api/19.21.0.247/jei-{MC}-common-api-19.21.0.247.pom",
     "JEI 编译期 API"),
]


def head(url, timeout):
    try:
        req = urllib.request.Request(url, method="HEAD",
                                     headers={"User-Agent": "greate-remake/verify-deps"})
        with urllib.request.urlopen(req, timeout=timeout) as r:
            return r.status, ""
    except urllib.error.HTTPError as e:
        # 有些仓库拒绝 HEAD 但允许 GET，405/403 视为"可能存在"，单独标注
        if e.code in (403, 405):
            try:
                req = urllib.request.Request(url, headers={"User-Agent": "greate-remake/verify-deps"})
                with urllib.request.urlopen(req, timeout=timeout) as r:
                    return r.status, ""
            except Exception as e2:
                return 0, str(e2)[:60]
        return e.code, str(e)[:60]
    except Exception as e:
        return 0, str(e)[:60]


def main():
    ap = argparse.ArgumentParser()
    ap.add_argument("--timeout", type=float, default=10.0)
    args = ap.parse_args()
    socket.setdefaulttimeout(args.timeout)

    with ThreadPoolExecutor(max_workers=8) as ex:
        results = list(ex.map(lambda e: (e, head(e[2], args.timeout)), ENTRIES))

    ok = fail = 0
    print("%-4s %-34s %-8s %s" % ("组", "制品", "状态", "说明"))
    print("-" * 96)
    for (grp, name, url, note), (status, err) in results:
        if status == 200:
            flag = "OK"
            ok += 1
        else:
            flag = "FAIL(%s)" % (status or "ERR")
            fail += 1
        print("%-4s %-34s %-8s %s" % (grp, name[:34], flag, note if status == 200 else (err or note)))

    print("-" * 96)
    print("可达 %d / 失败 %d（共 %d）" % (ok, fail, len(ENTRIES)))
    if fail:
        print("\n失败项请对照 DEPENDENCIES.md：")
        print("  · A 组失败 → 检查网络/代理，或确认版本号是否存在")
        print("  · B5/B7/B8 失败 → 见 DEPENDENCIES.md §E 的等价来源（jarJar 内嵌同一制品）")
    return 1 if fail else 0


if __name__ == "__main__":
    sys.exit(main())
