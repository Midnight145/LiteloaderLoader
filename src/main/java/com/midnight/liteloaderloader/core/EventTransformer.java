package com.midnight.liteloaderloader.core;

import org.spongepowered.asm.lib.ClassVisitor;
import org.spongepowered.asm.lib.MethodVisitor;
import org.spongepowered.asm.lib.Opcodes;

public class EventTransformer extends ClassVisitor {

    // The event handlers are populated in the `inject` method of the `Event` class.
    // We don't want to let liteloader do any actual injection, but we still need
    // it to populate all the event handlers.
    // What we do here is make it return immediately after populating but before the actual inject.

    public EventTransformer(ClassVisitor classVisitor) {
        super(Opcodes.ASM9, classVisitor);
    }

    @Override
    public MethodVisitor visitMethod(int access, String name, String descriptor, String signature,
        String[] exceptions) {
        if (name.equals("inject")) {
            MethodVisitor mv = super.visitMethod(access, name, descriptor, signature, exceptions);
            return new InjectMethodTransformer(mv);
        }
        return super.visitMethod(access, name, descriptor, signature, exceptions);
    }

    private static class InjectMethodTransformer extends MethodVisitor {

        public InjectMethodTransformer(MethodVisitor methodVisitor) {
            super(Opcodes.ASM9, methodVisitor);
        }

        @Override
        public void visitMethodInsn(int opcode, String owner, String name, String descriptor, boolean isInterface) {
            // getArgumentTypes is the method right after the event population
            if (opcode == Opcodes.INVOKESTATIC && "getArgumentTypes".equals(name)) {
                mv.visitVarInsn(Opcodes.ALOAD, 4);
                mv.visitInsn(Opcodes.ARETURN);
                return;
            }
            super.visitMethodInsn(opcode, owner, name, descriptor, isInterface);
        }
    }
}
