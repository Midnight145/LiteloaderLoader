package com.midnight.liteloaderfix.core;

import com.google.common.collect.ImmutableList;
import net.minecraft.launchwrapper.IClassTransformer;
import org.spongepowered.asm.lib.ClassReader;
import org.spongepowered.asm.lib.ClassVisitor;
import org.spongepowered.asm.lib.ClassWriter;
import org.spongepowered.asm.lib.FieldVisitor;
import org.spongepowered.asm.lib.Label;
import org.spongepowered.asm.lib.MethodVisitor;
import org.spongepowered.asm.lib.util.TraceClassVisitor;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

import static org.spongepowered.asm.lib.Opcodes.ALOAD;
import static org.spongepowered.asm.lib.Opcodes.ARETURN;
import static org.spongepowered.asm.lib.Opcodes.ASM9;
import static org.spongepowered.asm.lib.Opcodes.INVOKESPECIAL;
import static org.spongepowered.asm.lib.Opcodes.POP;
import static org.spongepowered.asm.lib.Opcodes.RETURN;

public class LiteloaderTransformer implements IClassTransformer {

    public List<String> blacklist = ImmutableList.of("com.mumfrey.liteloader.client.overlays.MinecraftOverlay");

    @Override
    public byte[] transform(String name, String transformedName, byte[] basicClass) {

        if (basicClass == null) return null;

        if (!transformedName.startsWith("com.mumfrey.liteloader") || transformedName.endsWith("Overlay") || transformedName.endsWith("IMinecraft")) {
            return basicClass;
        }
        System.out.println("Transforming " + name + " -> " + transformedName);

        byte[] transformedClass = applyTransformation(basicClass, transformedName);
        // Dump the transformed class to a file for debugging
        dumpClassToFile(transformedClass, "dumped/" + transformedName);

        return transformedClass;
    }

    private byte[] applyTransformation(byte[] basicClass, String transformedName) {
        ClassReader classReader = new ClassReader(basicClass);
        ClassWriter classWriter = new ClassWriter(0);
        ClassVisitor classVisitor = new LiteloaderClassVisitor(ASM9, classWriter, transformedName);
        classReader.accept(classVisitor, 0);

        return classWriter.toByteArray();
    }

    static String getReplacedString(String str) {
        // Moving from objectweb to spongepowered is almost a 1:1 mapping, except for it living in spongepowered/asm/lib.
        // There are a few one-off cases, which we replace separately at the end, but the first replace catches most of it.
        // Most of the time, we only need the first replacement, but doing all of them allows us to reuse this block everywhere.
        return str == null ? null : str.replace("org/objectweb/asm/", "org/spongepowered/asm/lib/").replace("RemappingAnnotationAdapter", "AnnotationRemapper")
            .replace("RemappingFieldAdapter", "FieldRemapper").replace("RemappingMethodAdapter", "MethodRemapper")
            .replace("RemappingClassAdapter", "ClassRemapper");
    }

    private static void dumpClassToFile(byte[] classBytes, String filePath) {
        try (FileWriter fileWriter = new FileWriter(filePath);
             PrintWriter printWriter = new PrintWriter(fileWriter)) {
            ClassReader classReader = new ClassReader(classBytes);
            TraceClassVisitor traceVisitor = new TraceClassVisitor(printWriter);
            classReader.accept(traceVisitor, 0);
        } catch (IOException ignored) {
        }
    }


    static class LiteloaderClassVisitor extends ClassVisitor {

        private final String transformedName;

        public LiteloaderClassVisitor(int api, ClassVisitor classVisitor, String transformedName) {
            super(api, classVisitor);
            this.transformedName = transformedName;
        }

        @Override
        public void visit(int version, int access, String name, String signature, String superName, String[] interfaces) {
            for (int i = 0; i < interfaces.length; i++) {
                interfaces[i] = getReplacedString(interfaces[i]);
            }
            super.visit(version, access, getReplacedString(name), getReplacedString(signature), getReplacedString(superName), interfaces);
        }

        @Override
        public FieldVisitor visitField(int access, String name, String desc, String signature, Object value) {
            return super.visitField(access, getReplacedString(name), getReplacedString(desc), getReplacedString(signature), value);
        }

        @Override
        public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
            MethodVisitor mv = super.visitMethod(access, getReplacedString(name), getReplacedString(desc), getReplacedString(signature), exceptions);
            return new LiteloaderMethodVisitor(api, mv, this.transformedName, name, desc);
        }
    }


    static class LiteloaderMethodVisitor extends MethodVisitor {

        private final String className;
        private final String name;
        private final String descriptor;

        public LiteloaderMethodVisitor(int api, MethodVisitor methodVisitor, String className, String name, String descriptor) {
            super(api, methodVisitor);
            this.className = className;
            this.name = name;
            this.descriptor = descriptor;
        }

        @Override
        public void visitMethodInsn(int opcode, String owner, String name, String descriptor, boolean isInterface) {
            if (opcode == INVOKESPECIAL && owner.endsWith("ClassWriter") && name.equals("<init>") && (descriptor.endsWith("I)V"))) {
                // Replace the constant argument
                super.visitInsn(POP); // Remove the original constant from the stack
                super.visitLdcInsn(ClassWriter.COMPUTE_MAXS); // Push the new constant (e.g., 0) onto the stack
            }

            super.visitMethodInsn(opcode, LiteloaderTransformer.getReplacedString(owner),
                LiteloaderTransformer.getReplacedString(name),
                LiteloaderTransformer.getReplacedString(descriptor), isInterface);
        }

        @Override
        public void visitLdcInsn(Object value) {
            if (value instanceof String str) {
                value = LiteloaderTransformer.getReplacedString(str);
            }
            super.visitLdcInsn(value);
        }

        @Override
        public void visitTypeInsn(int opcode, String type) {
            super.visitTypeInsn(opcode, LiteloaderTransformer.getReplacedString(type));
        }

        @Override
        public void visitFieldInsn(int opcode, String owner, String name, String descriptor) {
            super.visitFieldInsn(opcode, LiteloaderTransformer.getReplacedString(owner),
                LiteloaderTransformer.getReplacedString(name),
                LiteloaderTransformer.getReplacedString(descriptor));
        }

        @Override
        public void visitFrame(int type, int nLocal, Object[] local, int nStack, Object[] stack) {
            if (local != null) {
                for (int i = 0; i < local.length; i++) {
                    if (local[i] instanceof String str) {
                        local[i] = LiteloaderTransformer.getReplacedString(str);
                    }
                }
            }
            if (stack != null) {
                for (int i = 0; i < stack.length; i++) {
                    if (stack[i] instanceof String str) {
                        stack[i] = LiteloaderTransformer.getReplacedString(str);
                    }
                }
            }
            super.visitFrame(type, nLocal, local, nStack, stack);
        }

        @Override
        public void visitCode() {
            if (this.name.equals("transform") && this.className.endsWith("ClassOverlayTransformer")) {
                mv.visitVarInsn(ALOAD, 3);
                mv.visitInsn(ARETURN);
                return;
            }
            if (name.equals("postOverlayTransform") && this.className.endsWith("MinecraftOverlayTransformer")) {
                visitInsn(RETURN);
                return;
            }
            super.visitCode();
        }

        @Override
        public void visitLocalVariable(String name, String desc, String signature, Label start, Label end, int index) {
            super.visitLocalVariable(
                LiteloaderTransformer.getReplacedString(name),
                LiteloaderTransformer.getReplacedString(desc),
                signature != null ? LiteloaderTransformer.getReplacedString(signature) : null,
                start,
                end,
                index
            );
        }
    }
}
