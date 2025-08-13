package com.midnight.liteloaderloader.core.transformers.compat;

import static org.objectweb.asm.Opcodes.RETURN;

import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.MethodNode;

import com.midnight.liteloaderloader.core.transformers.ClassTransformer;

public class InputHandlerTransformer extends ClassTransformer {

    public InputHandlerTransformer() {
        super();
        methodTransforms.put("getBuffers", this::transformGetBuffers);
    }

    private void transformGetBuffers(MethodNode methodNode) {
        // just return immediately
        methodNode.instructions.insertBefore(methodNode.instructions.getFirst(), new InsnNode(RETURN));
    }
}
