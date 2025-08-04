package com.midnight.liteloaderloader.core.transformers;

import static com.midnight.liteloaderloader.core.LiteloaderLoader.LOG;
import static org.spongepowered.asm.lib.Opcodes.ALOAD;
import static org.spongepowered.asm.lib.Opcodes.INVOKESTATIC;
import static org.spongepowered.asm.lib.Opcodes.RETURN;

import org.spongepowered.asm.lib.tree.AbstractInsnNode;
import org.spongepowered.asm.lib.tree.ClassNode;
import org.spongepowered.asm.lib.tree.FrameNode;
import org.spongepowered.asm.lib.tree.InsnList;
import org.spongepowered.asm.lib.tree.InsnNode;
import org.spongepowered.asm.lib.tree.MethodInsnNode;
import org.spongepowered.asm.lib.tree.MethodNode;
import org.spongepowered.asm.lib.tree.VarInsnNode;

public class MinecraftOverlayTransformerTransformer extends ClassTransformer {

    public MinecraftOverlayTransformerTransformer() {
        super();
        methodTransforms.put("postOverlayTransform", this::transformPostOverlay);
        methodTransforms.put("transformStartGame", this::transformTransformStartGame);
    }

    private void transformTransformStartGame(MethodNode methodNode) {
        // Immediately return. We do this ourselves in MinecraftTransformer.
        methodNode.instructions.insertBefore(methodNode.instructions.getFirst(), new InsnNode(RETURN));
    }

    private void transformPostOverlay(MethodNode methodNode) {
        // The MinecraftOverlayTransformer doesn't properly remap `this` in its stack map frames, so `this` will still
        // be of type `com.mumfrey.liteloader.client.overlays.MinecraftOverlay` instead of
        // `net.minecraft.client.Minecraft`. We inject a method call to remap the stack map frames
        InsnList insns = new InsnList();
        insns.add(new VarInsnNode(ALOAD, 2)); // 2 is the ClassNode of Minecraft
        insns.add(
            new MethodInsnNode(
                INVOKESTATIC,
                "com/midnight/liteloaderloader/core/transformers/MinecraftOverlayTransformerTransformer",
                "remapStackMap",
                "(Lorg/spongepowered/asm/lib/tree/ClassNode;)V",
                false));
        insns.add(new InsnNode(RETURN));

        methodNode.instructions.insertBefore(methodNode.instructions.getFirst(), insns);
    }

    @SuppressWarnings("unused") // called by the injected code
    public static void remapStackMap(ClassNode cls) {
        for (MethodNode method : cls.methods) {
            for (AbstractInsnNode insn : method.instructions.toArray()) {
                if (insn instanceof FrameNode frame) {
                    for (int i = 0; i < frame.local.size(); i++) {
                        Object type = frame.local.get(i);
                        if (type instanceof String
                            && type.equals("com/mumfrey/liteloader/client/overlays/MinecraftOverlay")) {
                            LOG.info("Found MinecraftOverlay type in stack map for method: {}", method.name);
                            frame.local.set(i, "net/minecraft/client/Minecraft");
                        }
                    }
                }
            }
        }
    }
}
