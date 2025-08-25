package com.midnight.liteloaderloader.core.transformers;

import static com.midnight.liteloaderloader.core.LiteloaderLoader.LOG;

import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.TypeInsnNode;

public class ClassTransformerTransformer extends ClassTransformer {

    public ClassTransformerTransformer() {
        methodTransforms.put("writeClass", this::transformWriteClass);
    }

    private void transformWriteClass(MethodNode method) {
        final String objectWebClassWriter = "org/objectweb/asm/ClassWriter";
        final String safeClassWriter = "com/midnight/liteloaderloader/core/SafeClassWriter";

        for (AbstractInsnNode insn : method.instructions.toArray()) {
            if (insn.getOpcode() == NEW) {
                TypeInsnNode t = (TypeInsnNode) insn;
                if (objectWebClassWriter.equals(t.desc)) {
                    LOG.info("Transform: NEW ClassWriter -> SafeClassWriter in {}", method.name);
                    t.desc = safeClassWriter;
                }
            }

            else if (insn.getOpcode() == INVOKESPECIAL && insn instanceof MethodInsnNode m) {
                if (objectWebClassWriter.equals(m.owner) && "<init>".equals(m.name)) {
                    LOG.info("Transform: <init> owner ClassWriter -> SafeClassWriter in {}", method.name);
                    m.owner = safeClassWriter;
                }
            }
        }
    }

}
