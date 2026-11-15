# Voxy — unofficial Minecraft 1.21.1 port

> **Unofficial.** This is a community port of [Voxy](https://github.com/MCRcortex/voxy) by
> [MCRcortex](https://github.com/MCRcortex) to Minecraft **1.21.1** (Fabric). It is not affiliated with or
> supported by MCRcortex. Please don't report issues from this port upstream.
>
> Voxy is **All Rights Reserved** by MCRcortex (see [LICENSE.md](LICENSE.md)). This fork exists on GitHub
> under GitHub's forking terms; no builds are distributed from it.

**Voxy** is a far-distance LoD (Level of Detail) renderer for Minecraft that extends view distance far beyond
vanilla render distance at a low performance cost.

## Credits

| Role | Repository |
|---|---|
| Original mod | [MCRcortex/voxy](https://github.com/MCRcortex/voxy) |
| Earlier 1.21.1 work | [m3t4f1v3/voxy @ mc_1211](https://github.com/m3t4f1v3/voxy/tree/mc_1211) |
| 1.21.1 backport this builds on | [thijsi123/voxy @ mc_1211](https://github.com/thijsi123/voxy/tree/mc_1211) |

## What this port is

The `mc_1211` branch is the thijsi123 1.21.1 backport with upstream's `12111` branch (Voxy **0.2.16-beta**)
merged in, moved onto **Sodium 0.8.13** for 1.21.1. Notable changes over the earlier backport:

- Upstream rendering changes: new SSAO modes and auto-detection, depth handling rework, lightmap fix,
  view-bobbing-aware projection, and model-baking blending/seam fixes
- Sodium 0.8 config API menu (in Sodium's video settings), including the backport's fog and cloud options
- Iris 1.8.14 support (tested with Complementary Reimagined r5.9.3, which ships Voxy support)
- Nvidium is not supported: no Nvidium build for 1.21.1 works with Sodium 0.8

## Requirements

- Minecraft **1.21.1** (Java Edition), [Fabric Loader](https://fabricmc.net/) **0.20.1+**
- [Fabric API](https://modrinth.com/mod/fabric-api) `0.116.6+1.21.1`
- [Sodium](https://modrinth.com/mod/sodium) `mc1.21.1-0.8.13`
- Optional: [Iris](https://modrinth.com/mod/iris) `1.8.14-beta.1+1.21.1` with a shader pack that supports Voxy
- Java **21**

## Building from source

Requires JDK 21.

```bash
git clone -b mc_1211 https://github.com/chazwarp923/voxy
cd voxy
./gradlew build
# Output: build/libs/voxy-*.jar
```

`./gradlew runClient` starts a dev client. In dev, Iris is loaded from a locally patched copy to work around
a Fabric Loader dev-environment remapping crash (see `devIrisJar` in `build.gradle`); production is unaffected.

## Version info

| Property | Value |
|---|---|
| Voxy | `0.2.16-beta` (upstream `12111`) |
| Minecraft | `1.21.1` |
| Fabric Loader | `0.20.1` |
| Fabric API | `0.116.6+1.21.1` |
| Sodium | `0.8.13` |
| Iris | `1.8.14-beta.1` |
