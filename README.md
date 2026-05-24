# Voxy — Minecraft 1.21.1 Port

**Voxy** is a far-distance LoD (Level of Detail) rendering mod for Minecraft that dramatically extends your view distance without the normal performance cost.

This repository is a **1.21.1 backport** — the original creator ([MCRcortex](https://github.com/MCRcortex/voxy)) skipped 1.21.1 through 1.21.4 and only maintains newer versions. This port fills that gap.

---

## Sources / Credits

| Role | Repository |
|---|---|
| **Original mod** | [MCRcortex/voxy](https://github.com/MCRcortex/voxy) |
| **1.21.1 fork (base for this port)** | [m3t4f1v3/voxy @ mc_1211](https://github.com/m3t4f1v3/voxy/tree/mc_1211) |
| **This port** | [thijsi123/voxy](https://github.com/thijsi123/voxy) |

## What happened / how this was made

MCRcortex's `dev` branch had moved past Minecraft 1.21.1 to newer versions (`1.21.11`, `26.x`), leaving no working build for 1.21.1. Their `12111` branch targets a different version string (`1.21.11`) rather than the actual `1.21.1` release.

[m3t4f1v3](https://github.com/m3t4f1v3/voxy) had a proper `mc_1211` branch targeting real `minecraft_version=1.21.1` with community-merged fixes (fog rendering, config screen, etc.), but it was buried and not released as a build.

This repo was created with the help of **[Claude Code](https://claude.ai/code)** (Anthropic's AI coding assistant) which:
1. Cloned and analyzed both upstream repos (`MCRcortex/voxy` and `m3t4f1v3/voxy`)
2. Identified that `m3t4f1v3`'s `mc_1211` branch was the correct working 1.21.1 base (while MCRcortex's `12111` branch targets a different MC version entirely)
3. Created this clean working copy, configured it with portable Java (JDK 21), and verified it builds successfully
4. Published this repo and release

The mod compiled successfully on the first build attempt — no code changes were needed beyond confirming the right branch to use.

---

## Requirements

- **Minecraft**: 1.21.1 (Java Edition)
- **Mod Loader**: [Fabric](https://fabricmc.net/)
- **Required mods**:
  - [Fabric API](https://modrinth.com/mod/fabric-api) `0.116.6+1.21.1`
  - [Sodium](https://modrinth.com/mod/sodium) `mc1.21.1-0.6.13` (required)
  - [Lithium](https://modrinth.com/mod/lithium) `mc1.21.1-0.15.0` (required)
- **Java**: 21

---

## Installation

1. Install [Fabric Loader](https://fabricmc.net/use/) for Minecraft 1.21.1
2. Download the JAR from the [Releases](../../releases) page
3. Place it in your `.minecraft/mods/` folder alongside Fabric API, Sodium, and Lithium
4. Launch Minecraft 1.21.1 with the Fabric profile

---

## Building from source

Requires JDK 21.

```bash
git clone https://github.com/thijsi123/voxy
cd voxy
./gradlew build
# Output: build/libs/voxy-*.jar
```

---

## Version info

| Property | Value |
|---|---|
| Mod version | `0.2.14-alpha` |
| Minecraft | `1.21.1` |
| Fabric Loader | `0.17.2` |
| Fabric API | `0.116.6+1.21.1` |
| Sodium | `0.6.13` |
| Base commit | `f308c254` (m3t4f1v3/voxy mc_1211) |

---

*Original mod by [MCRcortex](https://github.com/MCRcortex). License: All-Rights-Reserved (see LICENSE.md). This port is provided as-is for the community.*
