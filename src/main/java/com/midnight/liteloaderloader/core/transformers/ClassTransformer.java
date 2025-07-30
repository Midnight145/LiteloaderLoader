package com.midnight.liteloaderloader.core.transformers;

import static com.midnight.liteloaderloader.core.LiteloaderLoader.LOG;

import java.util.HashMap;
import java.util.function.Consumer;

import org.spongepowered.asm.lib.ClassReader;
import org.spongepowered.asm.lib.ClassWriter;
import org.spongepowered.asm.lib.tree.ClassNode;
import org.spongepowered.asm.lib.tree.MethodNode;

public abstract class ClassTransformer {

    // Map of method names to transformation functions, where each function takes a MethodNode
    // and modifies it in place. Should be populated in the constructor of subclasses.
    // Iterated over in apply() to apply transformations to the class method-by-method.
    protected final HashMap<String, Consumer<MethodNode>> methodTransforms = new HashMap<>();

    public byte[] apply(byte[] classBytes) {
        ClassReader reader = new ClassReader(classBytes);
        ClassNode node = new ClassNode();
        reader.accept(node, 0);

        for (MethodNode method : node.methods) {
            if (methodTransforms.containsKey(method.name)) {
                LOG.info("Transforming method: {} in class: {}", method.name, node.name);
                int insCount = method.instructions.size();
                methodTransforms.get(method.name)
                    .accept(method);
                LOG.info(
                    "Transformed {}. Instruction count: {} (was {})",
                    method.name,
                    method.instructions.size(),
                    insCount);
            }
        }
        ClassWriter writer = new ClassWriter(0);
        node.accept(writer);
        return writer.toByteArray();
    }
}
