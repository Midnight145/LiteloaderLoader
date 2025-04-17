# LiteloaderLoader

This mod is a transformer and reimplementation of part of Liteloader for 1.7.10 to allow it to run alongside modern frameworks and mods, like RetroFuturaBootstrap and Angelica.

## Information

Liteloader uses an old version of objectweb ASM for it's class transforming. It, specifically it's use of COMPUTE_FRAMES, makes it incompatible with some modern mods. To fix this, I strip out all references to objectweb ASM and replace it with spongepowered ASM.

Liteloader uses some brittle ASM injections for event handling, involving runtime-generation of classes and methods. This mod reimplements those using mixins which are more stable, easier to work with, and less prone to compatibility issues with other mods.

Additionally, Liteloader uses the old obfuscated mappings, which stops it from being loaded in RFB. The problematic injections are replaced with mixins and the old injections are removed.

### Not Implemented

- For some reason, Liteloader unsets the final field on all fields in Block.class and Item.class. I'm not sure if this is necessary, so currently I'm not doing it.
- Currently, Liteloader's progress bar isn't being updated due to it requiring an intensive transformer that isn't implemented yet. This is a low priority as it doesn't affect the functionality of Liteloader.
- possibly some packet events?
