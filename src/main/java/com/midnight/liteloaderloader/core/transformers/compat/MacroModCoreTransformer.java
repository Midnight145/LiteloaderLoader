package com.midnight.liteloaderloader.core.transformers.compat;

import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;

import com.midnight.liteloaderloader.core.transformers.ClassTransformer;


public class MacroModCoreTransformer extends ClassTransformer {

    public MacroModCoreTransformer() {
        super();
        this.methodTransforms.put("transformKeyBindings", this::transformTransformKeyBindings);
    }

    private void transformTransformKeyBindings(MethodNode methodNode) {
        //
        int idx = 0;
        for (AbstractInsnNode insn : methodNode.instructions.toArray()) {
            if (insn.getOpcode() == INVOKEVIRTUAL) {
                if (idx == 1) {
                    MethodInsnNode newInsn = new MethodInsnNode(
                        INVOKESTATIC,
                        "com/midnight/liteloaderloader/core/runtime/RuntimeMethods",
                        "getKeyBindingEntries",
                        "(Lnet/minecraft/client/gui/GuiKeyBindingList;)[Lnet/minecraft/client/gui/GuiListExtended$IGuiListEntry;",
                        false);
                    methodNode.instructions.set(insn, newInsn);
                    return;
                }
                idx++;
            }
        }
    }

}
