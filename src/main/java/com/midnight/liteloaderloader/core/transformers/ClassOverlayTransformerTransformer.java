package com.midnight.liteloaderloader.core.transformers;

import static org.spongepowered.asm.lib.Opcodes.ALOAD;
import static org.spongepowered.asm.lib.Opcodes.ARETURN;
import static org.spongepowered.asm.lib.Opcodes.NEW;

import org.spongepowered.asm.lib.tree.AbstractInsnNode;
import org.spongepowered.asm.lib.tree.InsnList;
import org.spongepowered.asm.lib.tree.InsnNode;
import org.spongepowered.asm.lib.tree.MethodNode;
import org.spongepowered.asm.lib.tree.TypeInsnNode;
import org.spongepowered.asm.lib.tree.VarInsnNode;

public class ClassOverlayTransformerTransformer extends ClassTransformer {

    public ClassOverlayTransformerTransformer() {
        super();
        methodTransforms.put("transform", this::transformTransform);
    }

    private void transformTransform(MethodNode method) {
        // MinecraftOverlay gets passed into the transformer for some reason. This crashes the game, but we can't just
        // check for it early and return, otherwise the overlay won't be applied at all.
        // To avoid this, instead of throwing an exception, we just return. We have to do some other transformations
        // to MinecraftOverlayTransformer directly to make sure it works correctly as well.
        for (AbstractInsnNode insn : method.instructions.toArray()) {
            if (insn instanceof TypeInsnNode node) {
                if (node.getOpcode() == NEW && node.desc.equals("java/lang/RuntimeException")) {
                    // return directly before the RuntimeException is created
                    InsnList list = new InsnList();
                    list.add(new VarInsnNode(ALOAD, 3));
                    list.add(new InsnNode(ARETURN));
                    method.instructions.insertBefore(insn, list);
                }
            }
        }
    }
}
