package com.midnight.liteloaderloader.core.transformers;

import static org.spongepowered.asm.lib.Opcodes.ALOAD;
import static org.spongepowered.asm.lib.Opcodes.ARETURN;
import static org.spongepowered.asm.lib.Opcodes.F_SAME;
import static org.spongepowered.asm.lib.Opcodes.GETFIELD;
import static org.spongepowered.asm.lib.Opcodes.IFEQ;
import static org.spongepowered.asm.lib.Opcodes.INVOKEVIRTUAL;

import org.spongepowered.asm.lib.tree.AbstractInsnNode;
import org.spongepowered.asm.lib.tree.FieldInsnNode;
import org.spongepowered.asm.lib.tree.FrameNode;
import org.spongepowered.asm.lib.tree.InsnList;
import org.spongepowered.asm.lib.tree.InsnNode;
import org.spongepowered.asm.lib.tree.JumpInsnNode;
import org.spongepowered.asm.lib.tree.LabelNode;
import org.spongepowered.asm.lib.tree.LdcInsnNode;
import org.spongepowered.asm.lib.tree.MethodInsnNode;
import org.spongepowered.asm.lib.tree.MethodNode;
import org.spongepowered.asm.lib.tree.VarInsnNode;

public class ClassOverlayTransformerTransformer extends ClassTransformer {

    public ClassOverlayTransformerTransformer() {
        super();
        methodTransforms.put("ClassOverlayTransformer", this::transformTransform);
        methodTransforms.put("applyOverlay", this::transformApplyOverlay);
    }

    private void transformTransform(MethodNode method) {
        InsnList list = new InsnList();
        list.add(new VarInsnNode(ALOAD, 1)); // class name
        injectMinecraftOverlayCheck(list, 1); // index 1 is the return value
        AbstractInsnNode first = method.instructions.getFirst();
        method.instructions.insertBefore(first, list);
    }

    private void transformApplyOverlay(MethodNode method) {
        InsnList list = new InsnList();
        // class name exists at this.overlayClassName
        list.add(new VarInsnNode(ALOAD, 0)); // load this
        list.add( // get the overlayClassName field
            new FieldInsnNode(
                GETFIELD,
                "com/mumfrey/liteloader/transformers/ClassOverlayTransformer",
                "overlayClassName",
                "Ljava/lang/String;"));

        injectMinecraftOverlayCheck(list, 2); // index 2 is the return value
        AbstractInsnNode first = method.instructions.getFirst();
        method.instructions.insertBefore(first, list);
    }

    private void injectMinecraftOverlayCheck(InsnList list, int index) {
        list.add(new LdcInsnNode("com.mumfrey.liteloader.client.overlays.MinecraftOverlay"));
        list.add(new MethodInsnNode(INVOKEVIRTUAL, "java/lang/String", "equals", "(Ljava/lang/Object;)Z", false));
        LabelNode label = new LabelNode();
        list.add(new JumpInsnNode(IFEQ, label));
        // if the class name matches, return early
        list.add(new VarInsnNode(ALOAD, index));
        list.add(new InsnNode(ARETURN));

        // otherwise, continue with the original logic
        list.add(label);

        // frame to maintain stack state
        list.add(new FrameNode(F_SAME, 0, null, 0, null));
    }
}
