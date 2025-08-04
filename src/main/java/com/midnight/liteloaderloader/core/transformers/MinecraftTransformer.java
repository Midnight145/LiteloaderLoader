package com.midnight.liteloaderloader.core.transformers;

import static com.midnight.liteloaderloader.core.LiteloaderLoader.LOG;
import static org.spongepowered.asm.lib.Opcodes.INVOKESTATIC;

import org.spongepowered.asm.lib.Opcodes;
import org.spongepowered.asm.lib.tree.AbstractInsnNode;
import org.spongepowered.asm.lib.tree.MethodInsnNode;
import org.spongepowered.asm.lib.tree.MethodNode;
import org.spongepowered.asm.lib.tree.TypeInsnNode;

import com.mumfrey.liteloader.launch.LiteLoaderTweaker;

import cpw.mods.fml.common.ProgressManager;

public class MinecraftTransformer extends ClassTransformer {

    public MinecraftTransformer() {
        super();
        // This runs before Forge deobfuscates everything back to the srg mappings, so we use the obfuscated method name
        // Also seen in transformStartGame, which uses `blt` instead of `net.minecraft.client.renderer.EntityRenderer`
        methodTransforms.put("ag", this::transformStartGame);
    }

    private void transformStartGame(MethodNode methodNode) {
        for (AbstractInsnNode insn : methodNode.instructions.toArray()) {
            if (insn.getOpcode() == Opcodes.NEW) {
                TypeInsnNode typeInsn = (TypeInsnNode) insn;
                if (typeInsn.desc.equals("blt")) {
                    LOG.info("`new EntityRenderer` injection point found, injecting LiteLoader initialization.");

                    methodNode.instructions.insert(
                        insn,
                        new MethodInsnNode(
                            INVOKESTATIC,
                            "com/midnight/liteloaderloader/core/transformers/MinecraftTransformer",
                            "initMods",
                            "()V",
                            false));
                    return;
                }
            }
        }
        throw new RuntimeException("Failed to find EntityRenderer injection point in Minecraft startGame method");
    }

    @SuppressWarnings("unused") // called by the injected code
    public static void initMods() {
        ProgressManager.ProgressBar progressBar = ProgressManager.push("LiteLoader Mod Initialization", 1);
        LiteLoaderTweaker.init();
        LiteLoaderTweaker.postInit();
        progressBar.step("LiteLoader Mod Initialization Complete");
        ProgressManager.pop(progressBar);
    }
}
