package com.midnight.liteloaderloader.core.transformers;

import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.VarInsnNode;

import com.google.common.collect.ImmutableList;
import com.midnight.liteloaderloader.core.lib.ReflectionHelper;
import com.mumfrey.liteloader.core.LiteLoaderMods;

public class FMLCommonHandlerTransformer extends ClassTransformer {

    public FMLCommonHandlerTransformer() {
        methodTransforms.put("computeBranding", this::transformComputeBranding);
    }

    private void transformComputeBranding(MethodNode methodNode) {
        AbstractInsnNode lastAdd = null;
        // inject FMLCommonHandlerTransformer.addBranding(builder) after the last call to ImmutableList.Builder.add

        for (AbstractInsnNode insn : methodNode.instructions.toArray()) {
            if (insn.getOpcode() == Opcodes.INVOKEVIRTUAL) {
                MethodInsnNode min = (MethodInsnNode) insn;
                if (min.owner.equals("com/google/common/collect/ImmutableList$Builder") && min.name.equals("add")
                    && min.desc.equals("(Ljava/lang/Object;)Lcom/google/common/collect/ImmutableList$Builder;")) {
                    lastAdd = min;
                }
            }
        }

        if (lastAdd != null) {
            InsnList toInject = new InsnList();
            toInject.add(new VarInsnNode(Opcodes.ALOAD, 1));

            toInject.add(
                new MethodInsnNode(
                    Opcodes.INVOKESTATIC,
                    "com/midnight/liteloaderloader/core/transformers/FMLCommonHandlerTransformer",
                    "addBranding",
                    "(Lcom/google/common/collect/ImmutableList$Builder;)V",
                    false));

            methodNode.instructions.insert(lastAdd, toInject);
        }
    }

    @SuppressWarnings("unused") // called via asm
    public static void addBranding(ImmutableList.Builder<String> builder) {
        LiteLoaderMods llmods = ReflectionHelper.getLiteLoaderMods();
        int total = llmods.getAllMods()
            .size();
        int active = llmods.getLoadedMods()
            .size();
        builder.add(
            String.format(
                "%d LiteLoader mod%s loaded, %d mod%s active",
                total,
                total != 1 ? "s" : "",
                active,
                active != 1 ? "s" : ""));
    }
}
