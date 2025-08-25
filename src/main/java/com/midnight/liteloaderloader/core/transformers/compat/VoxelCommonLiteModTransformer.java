package com.midnight.liteloaderloader.core.transformers.compat;

import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.LdcInsnNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;

import com.midnight.liteloaderloader.core.transformers.ClassTransformer;

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
                if (methodInsnNode.getOpcode() == INVOKESTATIC) {
                    if (methodInsnNode.name.equals("getenv")) {
                        methodInsnNode.name = "getProperty";
                    }
                }
            }
        }
    }
}
