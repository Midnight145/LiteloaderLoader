package com.midnight.liteloaderloader.core.transformers.compat;

import org.spongepowered.asm.lib.tree.AbstractInsnNode;
import org.spongepowered.asm.lib.tree.LdcInsnNode;
import org.spongepowered.asm.lib.tree.MethodInsnNode;
import org.spongepowered.asm.lib.tree.MethodNode;

import com.midnight.liteloaderloader.core.transformers.ClassTransformer;

import scala.tools.asm.Opcodes;

public class VoxelCommonLiteModTransformer extends ClassTransformer {

    public VoxelCommonLiteModTransformer() {
        super();
        methodTransforms.put("<clinit>", this::transformStaticInit);
        methodTransforms.put("init", this::transformInit);
    }

    private void transformStaticInit(MethodNode methodNode) {
        for (AbstractInsnNode node : methodNode.instructions.toArray()) {
            if (node instanceof LdcInsnNode ldcNode) {
                if (ldcNode.cst.equals("TEMP")) {
                    ldcNode.cst = "java.io.tmpdir";
                    return;
                }
            }
        }
    }

    private void transformInit(MethodNode methodNode) {
        for (AbstractInsnNode node : methodNode.instructions.toArray()) {
            if (node instanceof MethodInsnNode methodInsnNode) {
                if (methodInsnNode.getOpcode() == Opcodes.INVOKESTATIC) {
                    if (methodInsnNode.name.equals("getenv")) {
                        methodInsnNode.name = "getProperty";
                    }
                }
            }
        }
    }
}
