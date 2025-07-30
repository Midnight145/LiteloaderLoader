package com.midnight.liteloaderloader.core.transformers;

import static org.spongepowered.asm.lib.Opcodes.ACONST_NULL;
import static org.spongepowered.asm.lib.Opcodes.ALOAD;
import static org.spongepowered.asm.lib.Opcodes.DUP;
import static org.spongepowered.asm.lib.Opcodes.ICONST_0;
import static org.spongepowered.asm.lib.Opcodes.INVOKESPECIAL;
import static org.spongepowered.asm.lib.Opcodes.INVOKEVIRTUAL;
import static org.spongepowered.asm.lib.Opcodes.NEW;

import org.spongepowered.asm.lib.tree.InsnList;
import org.spongepowered.asm.lib.tree.InsnNode;
import org.spongepowered.asm.lib.tree.MethodInsnNode;
import org.spongepowered.asm.lib.tree.MethodNode;
import org.spongepowered.asm.lib.tree.TypeInsnNode;
import org.spongepowered.asm.lib.tree.VarInsnNode;

public class EventTransformer extends ClassTransformer {

    public EventTransformer() {
        super();
        methodTransforms.put("injectCancellationCode", this::transformInjectCancellationCode);
    }

    private void transformInjectCancellationCode(MethodNode method) {
        InsnList instructions = new InsnList();

        // Inject `insns.add(new FrameNode(F_SAME, 0, null, 0, null));`

        // ALOAD <index of `insns`>
        instructions.add(new VarInsnNode(ALOAD, 1));

        // new FrameNode
        instructions.add(new TypeInsnNode(NEW, "org/spongepowered/asm/lib/tree/FrameNode"));
        // Ref gets consumed by constructor, so we need to duplicate it
        instructions.add(new InsnNode(DUP));

        // FrameNode constructor parameters
        instructions.add(new InsnNode(ICONST_0)); // type = Opcodes.F_SAME
        instructions.add(new InsnNode(ICONST_0)); // numLocal = 0
        instructions.add(new InsnNode(ACONST_NULL)); // local[] = null
        instructions.add(new InsnNode(ICONST_0)); // numStack = 0
        instructions.add(new InsnNode(ACONST_NULL)); // stack[] = null

        // Call FrameNode constructor
        instructions.add(
            new MethodInsnNode(
                INVOKESPECIAL,
                "org/spongepowered/asm/lib/tree/FrameNode",
                "<init>",
                "(II[Ljava/lang/Object;I[Ljava/lang/Object;)V",
                false));

        // Call insns.add(FrameNode)
        instructions.add(
            new MethodInsnNode(
                INVOKEVIRTUAL,
                "org/spongepowered/asm/lib/tree/InsnList",
                "add",
                "(Lorg/spongepowered/asm/lib/tree/AbstractInsnNode;)V",
                false));

        // Insert at end of method
        // last two instructions are the return and ATHROW instructions, so we insert at size - 2
        method.instructions.insertBefore(method.instructions.get(method.instructions.size() - 2), instructions);
    }
}
