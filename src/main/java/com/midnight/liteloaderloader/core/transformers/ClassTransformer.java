package com.midnight.liteloaderloader.core.transformers;

import static com.midnight.liteloaderloader.core.LiteloaderLoader.LOG;

import java.util.HashMap;
import java.util.function.Consumer;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.MethodNode;

import com.midnight.liteloaderloader.core.SafeClassWriter;

public abstract class ClassTransformer implements Opcodes {

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
        ClassWriter writer = new SafeClassWriter(ClassWriter.COMPUTE_MAXS | ClassWriter.COMPUTE_FRAMES);
        node.accept(writer);
        return writer.toByteArray();
    }

    public byte[] apply(byte[] classBytes, boolean load) {
        return load ? apply(classBytes) : classBytes;
    }
}
