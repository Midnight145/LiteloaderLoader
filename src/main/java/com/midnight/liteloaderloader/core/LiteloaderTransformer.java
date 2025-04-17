package com.midnight.liteloaderloader.core;

import static com.midnight.liteloaderloader.core.LiteloaderLoader.LOG;
import static org.spongepowered.asm.lib.Opcodes.ALOAD;
import static org.spongepowered.asm.lib.Opcodes.ARETURN;
import static org.spongepowered.asm.lib.Opcodes.ASM9;
import static org.spongepowered.asm.lib.Opcodes.INVOKESPECIAL;
import static org.spongepowered.asm.lib.Opcodes.POP;
import static org.spongepowered.asm.lib.Opcodes.RETURN;

import java.util.Arrays;
import java.util.HashMap;

import net.minecraft.launchwrapper.IClassTransformer;

import org.spongepowered.asm.lib.ClassReader;
import org.spongepowered.asm.lib.ClassVisitor;
import org.spongepowered.asm.lib.ClassWriter;
import org.spongepowered.asm.lib.FieldVisitor;
import org.spongepowered.asm.lib.Label;
import org.spongepowered.asm.lib.MethodVisitor;

import com.midnight.liteloaderloader.lib.Tuple;

@SuppressWarnings("unused")
public class LiteloaderTransformer implements IClassTransformer {

    // This is a hashmap of classes with methods that need to return immediately.
    // The key is the class name, and the value is a tuple of the method name and a tuple of the instruction to replace
    // The nested tuple contains the type of return instruction (either ARETURN or RETURN) and the index of the
    // parameter to return or null if not applicable.
    private static final HashMap<String, Tuple<String, Tuple<Integer, Integer>>> toKill = new HashMap<>();

    static {
        toKill.put("ClassOverlayTransformer", Tuple.of("transform", Tuple.of(ARETURN, 3)));
        toKill.put("CrashReportTransformer", Tuple.of("transform", Tuple.of(ARETURN, 3)));
        toKill.put("MinecraftOverlayTransformer", Tuple.of("postOverlayTransform", Tuple.of(RETURN, null)));
    }

    @Override
    public byte[] transform(String name, String transformedName, byte[] basicClass) {

        if (basicClass == null) return null;

        if (!transformedName.startsWith("com.mumfrey.liteloader")) {
            return basicClass;
        }

        byte[] transformedClass = applyTransformation(basicClass, transformedName);
        if (!Arrays.equals(transformedClass, basicClass)) {
            LOG.info("Transformed {}", transformedName);
        }

        return transformedClass;
    }

    private byte[] applyTransformation(byte[] basicClass, String transformedName) {
        ClassReader classReader = new ClassReader(basicClass);
        ClassWriter classWriter = new ClassWriter(0);

        if (transformedName.endsWith("Event")) {
            ClassVisitor second = new EventTransformer(classWriter);
            ClassVisitor first = new LiteloaderClassVisitor(ASM9, second, transformedName);
            classReader.accept(first, 0);
            return classWriter.toByteArray();
        }

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

        private final String className;
        private final String name;

        public LiteloaderMethodVisitor(int api, MethodVisitor methodVisitor, String className, String name) {
            super(api, methodVisitor);
            this.className = className;
            this.name = name;
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
        public void visitCode() {
            // This will return early if the method is in toKill
            String truncatedName = getClassName(this.className);
            if (toKill.containsKey(truncatedName)) {
                // The String is the method name
                Tuple<String, Tuple<Integer, Integer>> tuple = toKill.get(truncatedName);
                // It's more accurately Tuple<OPCODE, Integer>, where OPCODE is a return opcode.
                Tuple<Integer, Integer> instructionInfo = tuple.getSecond();
                if (this.name.equals(tuple.getFirst())) {
                    if (instructionInfo.getFirst() == ARETURN) {
                        visitVarInsn(ALOAD, instructionInfo.getSecond());
                        visitInsn(ARETURN);
                    } else if (instructionInfo.getFirst() == RETURN) {
                        visitInsn(RETURN);
                    }
                    return;
                }
            }
            super.visitCode();
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

        private static String getClassName(String name) {
            // This will just get the class name from a qualified name, eg.
            // com.example.TestClass -> TestClass
            int lastDotIndex = name.lastIndexOf('.');
            return lastDotIndex == -1 ? name : name.substring(lastDotIndex + 1);
        }
    }
}
