package com.midnight.liteloaderloader.core.transformers;

import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.FieldInsnNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.VarInsnNode;

public class GuiModListTransformer extends ClassTransformer {

    public GuiModListTransformer() {
        methodTransforms.put("<init>", this::transformInit);
    }

    private void transformInit(MethodNode methodNode) {
        for (AbstractInsnNode insn : methodNode.instructions.toArray()) {
            if (insn.getOpcode() == Opcodes.RETURN) {
                // LiteloaderModContainer.populate(this.mods);
                InsnList list = new InsnList();
                list.add(new VarInsnNode(Opcodes.ALOAD, 0));
                list.add(
                    new FieldInsnNode(
                        Opcodes.GETFIELD,
                        "cpw/mods/fml/client/GuiModList",
                        "mods",
                        "Ljava/util/ArrayList;"));
                list.add(
                    new MethodInsnNode(
                        Opcodes.INVOKESTATIC,
                        "com/midnight/liteloaderloader/core/lib/LiteloaderModContainer",
                        "populate",
                        "(Ljava/util/ArrayList;)V",
                        false));
                methodNode.instructions.insertBefore(insn, list);
            }
        }
    }
}
