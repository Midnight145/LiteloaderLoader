package com.midnight.liteloaderloader.core;

import static com.midnight.liteloaderloader.core.LiteloaderLoader.LOG;
import static org.spongepowered.asm.lib.Opcodes.ASM9;
import static org.spongepowered.asm.lib.Opcodes.INVOKESPECIAL;
import static org.spongepowered.asm.lib.Opcodes.POP;

import java.util.Arrays;
import java.util.HashMap;
import java.util.function.Function;

import net.minecraft.launchwrapper.IClassTransformer;

import org.spongepowered.asm.lib.ClassReader;
import org.spongepowered.asm.lib.ClassVisitor;
import org.spongepowered.asm.lib.ClassWriter;
import org.spongepowered.asm.lib.FieldVisitor;
import org.spongepowered.asm.lib.Label;
import org.spongepowered.asm.lib.MethodVisitor;

import com.midnight.liteloaderloader.core.transformers.ClassOverlayTransformerTransformer;
import com.midnight.liteloaderloader.core.transformers.EventTransformer;
import com.midnight.liteloaderloader.core.transformers.MinecraftOverlayTransformerTransformer;
import com.midnight.liteloaderloader.core.transformers.MinecraftTransformer;
import com.midnight.liteloaderloader.core.transformers.compat.AngelicaHUDCachingTransformer;
import com.midnight.liteloaderloader.core.transformers.compat.InputHandlerTransformer;
import com.midnight.liteloaderloader.core.transformers.compat.VoxelCommonLiteModTransformer;
import com.midnight.liteloaderloader.lib.Tuple;

@SuppressWarnings("unused")
public class LiteloaderTransformer implements IClassTransformer {

    // spotless:off
    private static final String[] transformedNamespaces = new String[] {
        // Liteloader base classes
        "com.mumfrey.liteloader",
        // Classes for Macro Keybind Mod, required for compatibility.
        "net.eq2online.macros"
    };

    // spotless:on

    // This is a hashmap of classes with methods that need to return immediately.
    // The key is the class name, and the value is a tuple of the method name and a tuple of the instruction to replace.
    // The nested tuple contains the type of return instruction (either ARETURN or RETURN) and the index of the
    // parameter to return or null if not applicable.
    private static final HashMap<String, Tuple<String, Tuple<Integer, Integer>>> toKill = new HashMap<>();

    // One-off transformations that we want to apply to specific classes.
    // Function should be the apply method of a ClassTransformer subclass
    private static final HashMap<String, Function<byte[], byte[]>> transformations = new HashMap<>();

    static {
        // Angelica's HUD Caching option overrides EntityRenderer.updateCameraAndRender, which is used for several
        // events. We need to apply this transformer to it to call throw events ourselves.
        transformations.put(
            "com.gtnewhorizons.angelica.hudcaching.HUDCaching",
            bytes -> new AngelicaHUDCachingTransformer().apply(bytes));

        // The Liteloader event transformer is broken due to frames not being computed properly, even without stripping
        // the COMPUTE_FRAMES flag from its ClassWriter, which we do later.
        // This transformer manually injects a frame into the instruction list.
        transformations
            .put("com.mumfrey.liteloader.transformers.event.Event", bytes -> new EventTransformer().apply(bytes));

        // For some reason, MinecraftOverlay is passed into the ClassOverlayTransformer, which causes a crash.
        // We add a check that immediately returns if the current class is MinecraftOverlay.
        transformations.put(
            "com.mumfrey.liteloader.transformers.ClassOverlayTransformer",
            bytes -> new ClassOverlayTransformerTransformer().apply(bytes));

        // VoxelCommonLiteMod uses a hardcoded TEMP environment variable, which only exists on Windows.
        // We replace it with java.io.tmpdir property, which is the standard temporary directory for Java.
        transformations.put(
            "com.thevoxelbox.common.VoxelCommonLiteMod",
            bytes -> new VoxelCommonLiteModTransformer().apply(bytes));

        // The MinecraftOverlayTransformer has a lot of issues running under new toolings. It fails to remap `this`
        // from MinecraftOverlay to Minecraft, which causes a crash. Additionally, it doesn't properly handle frames
        // when it does the startGame transform to initialize LiteLoader.
        // We remap `this` to Minecraft, and kill the startGame transform and do it ourselves in MinecraftTransformer.
        transformations.put(
            "com.mumfrey.liteloader.client.transformers.MinecraftOverlayTransformer",
            bytes -> new MinecraftOverlayTransformerTransformer().apply(bytes));

        // LiteLoader's MinecraftOverlayTransformer is broken and doesn't properly calculate stack frames, so we inject
        // Liteloader initialization functions instead of letting Liteloader do it.
        transformations.put("net.minecraft.client.Minecraft", bytes -> new MinecraftTransformer().apply(bytes));

        // InputHandler tries to resolve lwjgl2-only fields, which spams exceptions in the log. It mostly works either
        // way, so we just return immediately in the getBuffers method to avoid the spam.
        transformations
            .put("net.eq2online.macros.input.InputHandler", bytes -> new InputHandlerTransformer().apply(bytes));
    }

    @Override
    public byte[] transform(String name, String transformedName, byte[] basicClass) {
        if (basicClass == null) return null;
        if (transformations.containsKey(transformedName)) {
            LOG.info("Applying transformation for {}", transformedName);
            basicClass = transformations.get(transformedName)
                .apply(basicClass);
        }

        for (String namespace : transformedNamespaces) {
            if (transformedName.startsWith(namespace)) {
                byte[] transformedClass = applyTransformation(basicClass, transformedName);
                if (!Arrays.equals(transformedClass, basicClass)) {
                    LOG.info("Transformed {}", transformedName);
                }

                return transformedClass;
            }
        }

        return basicClass;
    }

    private byte[] applyTransformation(byte[] basicClass, String transformedName) {
        ClassReader classReader = new ClassReader(basicClass);
        ClassWriter classWriter = new ClassWriter(ClassWriter.COMPUTE_MAXS | ClassWriter.COMPUTE_FRAMES);

        ClassVisitor classVisitor = new LiteloaderClassVisitor(ASM9, classWriter, transformedName);
        classReader.accept(classVisitor, 0);

        return classWriter.toByteArray();
    }

    static String remapString(String str) {
        // Moving from objectweb to spongepowered is almost a 1:1 mapping, except for it living in
        // spongepowered/asm/lib.
        // There are a few one-off cases, which we replace separately at the end, but the first replace catches most of
        // it.
        // Most of the time, we only need the first replacement, but doing all of them allows us to reuse this block
        // everywhere.
        return str == null ? null
            : str.replace("org/objectweb/asm/", "org/spongepowered/asm/lib/")
                .replace("RemappingAnnotationAdapter", "AnnotationRemapper")
                .replace("RemappingFieldAdapter", "FieldRemapper")
                .replace("RemappingMethodAdapter", "MethodRemapper")
                .replace("RemappingClassAdapter", "ClassRemapper");
    }

    // Basically, we want to remap all strings that might have references to objectweb asm to spongepowered asm.
    // For the most part, everything is just super() calls with the arguments being passed to
    // remapString when necessary, but there's a few one-offs that we need to handle
    static class LiteloaderClassVisitor extends ClassVisitor {

        private final String transformedName;

        public LiteloaderClassVisitor(int api, ClassVisitor classVisitor, String transformedName) {
            super(api, classVisitor);
            this.transformedName = transformedName;
        }

        @Override
        public void visit(int version, int access, String name, String signature, String superName,
            String[] interfaces) {
            for (int i = 0; i < interfaces.length; i++) {
                interfaces[i] = remapString(interfaces[i]);
            }
            super.visit(version, access, remapString(name), remapString(signature), remapString(superName), interfaces);
        }

        @Override
        public FieldVisitor visitField(int access, String name, String desc, String signature, Object value) {
            return super.visitField(access, remapString(name), remapString(desc), remapString(signature), value);
        }

        @Override
        public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
            MethodVisitor mv = super.visitMethod(
                access,
                remapString(name),
                remapString(desc),
                remapString(signature),
                exceptions);
            return new LiteloaderMethodVisitor(api, mv, this.transformedName, name);
        }
    }

    // Essentially the same thing as the LiteloaderClassVisitor, but for methods.
    // We do also make a modification to ClassWriter<init> calls to strip COMPUTE_FRAMES from the constant passed.
    static class LiteloaderMethodVisitor extends MethodVisitor {

        public LiteloaderMethodVisitor(int api, MethodVisitor methodVisitor, String className, String name) {
            super(api, methodVisitor);
        }

        @Override
        public void visitMethodInsn(int opcode, String owner, String name, String descriptor, boolean isInterface) {
            if (opcode == INVOKESPECIAL && owner.endsWith("ClassWriter")
            // The original ClassWriter constructor calls use ClassWriter.COMPUTE_MAXS | ClassWriter.COMPUTE_FRAMES.
            // We want to remove the COMPUTE_FRAMES part, so we check if the descriptor ends with I)V, so it
            // will target both constructors.
                && name.equals("<init>")
                && (descriptor.endsWith("I)V"))) {

                super.visitInsn(POP);
                super.visitLdcInsn(ClassWriter.COMPUTE_MAXS);
            }

            super.visitMethodInsn(
                opcode,
                LiteloaderTransformer.remapString(owner),
                LiteloaderTransformer.remapString(name),
                LiteloaderTransformer.remapString(descriptor),
                isInterface);
        }

        @Override
        public void visitLdcInsn(Object value) {
            if (value instanceof String str) {
                value = LiteloaderTransformer.remapString(str);
            }
            super.visitLdcInsn(value);
        }

        @Override
        public void visitTypeInsn(int opcode, String type) {
            super.visitTypeInsn(opcode, LiteloaderTransformer.remapString(type));
        }

        @Override
        public void visitFieldInsn(int opcode, String owner, String name, String descriptor) {
            super.visitFieldInsn(
                opcode,
                LiteloaderTransformer.remapString(owner),
                LiteloaderTransformer.remapString(name),
                LiteloaderTransformer.remapString(descriptor));
        }

        @Override
        public void visitFrame(int type, int nLocal, Object[] local, int nStack, Object[] stack) {
            if (local != null) {
                for (int i = 0; i < local.length; i++) {
                    if (local[i] instanceof String str) {
                        local[i] = LiteloaderTransformer.remapString(str);
                    }
                }
            }
            if (stack != null) {
                for (int i = 0; i < stack.length; i++) {
                    if (stack[i] instanceof String str) {
                        stack[i] = LiteloaderTransformer.remapString(str);
                    }
                }
            }
            super.visitFrame(type, nLocal, local, nStack, stack);
        }

        @Override
        public void visitLocalVariable(String name, String desc, String signature, Label start, Label end, int index) {
            super.visitLocalVariable(
                LiteloaderTransformer.remapString(name),
                LiteloaderTransformer.remapString(desc),
                signature != null ? LiteloaderTransformer.remapString(signature) : null,
                start,
                end,
                index);
        }

    }
}
