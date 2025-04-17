package com.midnight.liteloaderfix.core;

import static org.spongepowered.asm.lib.Opcodes.ASM9;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

import net.minecraft.launchwrapper.IClassTransformer;

import org.spongepowered.asm.lib.ClassReader;
import org.spongepowered.asm.lib.ClassVisitor;
import org.spongepowered.asm.lib.ClassWriter;
import org.spongepowered.asm.lib.FieldVisitor;
import org.spongepowered.asm.lib.Label;
import org.spongepowered.asm.lib.MethodVisitor;
import org.spongepowered.asm.lib.util.TraceClassVisitor;

import com.google.common.collect.ImmutableList;

public class LiteloaderTransformer_old implements IClassTransformer {

    private List<String> noTransform = ImmutableList.of(
        "com.mumfrey.liteloader.client.transformers.MinecraftOverlayTransformer",
        "com.mumfrey.liteloader.client.transformers.CrashReportTransformer",
        "com.mumfrey.liteloader.launch.LiteloaderTransformer");

    @Override
    public byte[] transform(String name, String transformedName, byte[] basicClass) {
        if (basicClass == null) return null;
        if (!transformedName.startsWith("com.mumfrey.liteloader") && !noTransform.contains(transformedName)) {
            return basicClass;
        }
        System.out.println("Transforming " + name + " -> " + transformedName);

        byte[] transformedClass = applyTransformation(basicClass);
        // Dump the transformed class to a file for debugging
        dumpClassToFile(transformedClass, "dumped/" + transformedName);

        return transformedClass;
    }

    private byte[] applyTransformation(byte[] basicClass) {
        ClassReader classReader = new ClassReader(basicClass);
        ClassWriter classWriter = new ClassWriter(ClassWriter.COMPUTE_FRAMES);
        ClassVisitor classVisitor = new LiteloaderClassVisitor(ASM9, classWriter);
        classReader.accept(classVisitor, 0);

        return classWriter.toByteArray();
    }

    static String getReplacedString(String str) {
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

    private static void dumpClassToFile(byte[] classBytes, String filePath) {
        try (FileWriter fileWriter = new FileWriter(filePath); PrintWriter printWriter = new PrintWriter(fileWriter)) {
            ClassReader classReader = new ClassReader(classBytes);
            TraceClassVisitor traceVisitor = new TraceClassVisitor(printWriter);
            classReader.accept(traceVisitor, 0);
        } catch (IOException ignored) {}
    }

    static class LiteloaderClassVisitor extends ClassVisitor {

        public LiteloaderClassVisitor(int api, ClassVisitor classVisitor) {
            super(api, classVisitor);
        }

        @Override
        public void visit(int version, int access, String name, String signature, String superName,
            String[] interfaces) {
            for (int i = 0; i < interfaces.length; i++) {
                interfaces[i] = getReplacedString(interfaces[i]);
            }
            super.visit(
                version,
                access,
                getReplacedString(name),
                getReplacedString(signature),
                getReplacedString(superName),
                interfaces);
        }

        @Override
        public FieldVisitor visitField(int access, String name, String desc, String signature, Object value) {
            return super.visitField(
                access,
                getReplacedString(name),
                getReplacedString(desc),
                getReplacedString(signature),
                value);
        }

        @Override
        public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
            MethodVisitor mv = super.visitMethod(
                access,
                getReplacedString(name),
                getReplacedString(desc),
                getReplacedString(signature),
                exceptions);
            return new LiteloaderMethodVisitor(api, mv);
        }
    }

    static class LiteloaderMethodVisitor extends MethodVisitor {

        public LiteloaderMethodVisitor(int api, MethodVisitor methodVisitor) {
            super(api, methodVisitor);
        }

        @Override
        public void visitMethodInsn(int opcode, String owner, String name, String descriptor, boolean isInterface) {
            super.visitMethodInsn(
                opcode,
                LiteloaderTransformer_old.getReplacedString(owner),
                LiteloaderTransformer_old.getReplacedString(name),
                LiteloaderTransformer_old.getReplacedString(descriptor),
                isInterface);
        }

        @Override
        public void visitLdcInsn(Object value) {
            if (value instanceof String str) {
                value = LiteloaderTransformer_old.getReplacedString(str);
            }
            super.visitLdcInsn(value);
        }

        @Override
        public void visitTypeInsn(int opcode, String type) {
            super.visitTypeInsn(opcode, LiteloaderTransformer_old.getReplacedString(type));
        }

        @Override
        public void visitFieldInsn(int opcode, String owner, String name, String descriptor) {
            super.visitFieldInsn(
                opcode,
                LiteloaderTransformer_old.getReplacedString(owner),
                LiteloaderTransformer_old.getReplacedString(name),
                LiteloaderTransformer_old.getReplacedString(descriptor));
        }

        @Override
        public void visitFrame(int type, int nLocal, Object[] local, int nStack, Object[] stack) {
            if (local != null) {
                for (int i = 0; i < local.length; i++) {
                    if (local[i] instanceof String str) {
                        local[i] = LiteloaderTransformer_old.getReplacedString(str);
                    }
                }
            }
            if (stack != null) {
                for (int i = 0; i < stack.length; i++) {
                    if (stack[i] instanceof String str) {
                        stack[i] = LiteloaderTransformer_old.getReplacedString(str);
                    }
                }
            }
            super.visitFrame(type, nLocal, local, nStack, stack);
        }

        @Override
        public void visitLocalVariable(String name, String desc, String signature, Label start, Label end, int index) {
            super.visitLocalVariable(
                LiteloaderTransformer_old.getReplacedString(name),
                LiteloaderTransformer_old.getReplacedString(desc),
                signature != null ? LiteloaderTransformer_old.getReplacedString(signature) : null,
                start,
                end,
                index);
        }
    }
}
