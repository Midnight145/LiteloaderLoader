package com.midnight.liteloaderloader.core.transformers.loadingbar;

import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.TypeInsnNode;

import com.midnight.liteloaderloader.core.transformers.ClassTransformer;

public class ObjectFactoryClientTransformer extends ClassTransformer {

    public ObjectFactoryClientTransformer() {
        super();
        methodTransforms.put("preBeginGame", this::transformPreBeginGame);
    }

    private void transformPreBeginGame(MethodNode method) {
        method.instructions.clear();
        method.instructions.add(new TypeInsnNode(NEW, "com/midnight/liteloaderloader/core/lib/LLLLoadingBar"));
        method.instructions.add(
            new MethodInsnNode(
                INVOKESPECIAL,
                "com/midnight/liteloaderloader/core/lib/LLLLoadingBar",
                "<init>",
                "()V",
                false));
        method.instructions.add(new InsnNode(RETURN));
    }
}
