# LiteloaderLoader

This mod is a transformer and reimplementation of part of Liteloader for 1.7.10 to allow it to run alongside modern frameworks and mods, like RetroFuturaBootstrap and Angelica.

## Information

Liteloader's use of COMPUTE_FRAMES in its class transforming is unsafe, occasionally loading classes that are not fully initialized. I replace LiteLoader's usage of ObjectWeb's ClassWriter with my own SafeClassWriter, which ensures safe frame computation.

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
