# LiteloaderLoader

This mod is a transformer and reimplementation of part of Liteloader for 1.7.10 to allow it to run alongside modern frameworks and mods, like RetroFuturaBootstrap and Angelica.

## Information

Liteloader's use of COMPUTE_FRAMES in its class transforming is unsafe, occasionally loading classes that are not fully initialized. This replaces LiteLoader's usage of ObjectWeb's ClassWriter with a custom SafeClassWriter, which ensures safe frame computation.

Other fixes:
- Any mod extending from VoxelCommonLiteMod on non-Windows platforms, replacing its use of the TEMP environment variable with java.io.tmpdir.
- Angelica's HUD Caching module not throwing several pre/postrender events causing mods like VoxelMap not to render.
- Macro Keybind Mod spamming the logfile with errors under lwjgl3ify due to missing fields.

Other minor changes or improvements:
- The LiteLoader progress bar has been reimplemented with a Forge progress bar while modloading.
- LiteLoader mods are added to the ingame mod list
- Branding is added to the main menu to show mod count similar to Forge

**Note:** This is **not** a full replacement for Liteloader, it is designed to run alongside it. You still need Liteloader itself.

## Installation

See [INSTALLATION.md](./INSTALLATION.md)

## Requirements

- Liteloader being run as a mod, not installed separately alongside Forge (eg. through the Versions tab in MultiMC or its derivatives)
  - See the [installation instructions](./INSTALLATION.md)

### Not Implemented

- For some reason, Liteloader unsets the final field on all fields in Block.class and Item.class. I'm not sure if this is necessary, so currently I'm not doing it.
- Liteloader standalone installs, as opposed to being loaded via Forge as a mod.

## Known Issues

- Occasionally, the game will hang on startup. This can be resolved by restarting the game, though in rare cases it may take multiple tries.
- There's some progress bar jankiness on startup, but this is not a major issue and should not affect gameplay.
