package com.midnight.liteloaderloader.core.transformers.loadingbar;

import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.MethodNode;

import com.midnight.liteloaderloader.core.transformers.ClassTransformer;

public class LoadingBarTransformer extends ClassTransformer {

    public LoadingBarTransformer() {
        methodTransforms.put("render", this::killRender);
    }

    private void killRender(MethodNode methodNode) {
        methodNode.instructions.insertBefore(methodNode.instructions.getFirst(), new InsnNode(Opcodes.RETURN));
    }
}
