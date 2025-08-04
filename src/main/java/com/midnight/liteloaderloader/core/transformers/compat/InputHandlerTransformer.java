package com.midnight.liteloaderloader.core.transformers.compat;

import static org.spongepowered.asm.lib.Opcodes.RETURN;

import org.spongepowered.asm.lib.tree.InsnNode;
import org.spongepowered.asm.lib.tree.MethodNode;

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
