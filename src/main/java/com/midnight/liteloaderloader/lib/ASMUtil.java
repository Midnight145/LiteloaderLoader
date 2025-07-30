package com.midnight.liteloaderloader.lib;

import static org.spongepowered.asm.lib.Opcodes.RETURN;

import org.spongepowered.asm.lib.tree.AbstractInsnNode;
import org.spongepowered.asm.lib.tree.InsnNode;
import org.spongepowered.asm.lib.tree.MethodNode;

public class ASMUtil {

    public static void killMethod(MethodNode target, int opcode, int index) {

    }

    public static void killMethod(MethodNode target) {
        AbstractInsnNode insn = target.instructions.getFirst();
        target.instructions.insertBefore(insn, new InsnNode(RETURN));
    }
}
