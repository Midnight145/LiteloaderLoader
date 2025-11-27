package com.midnight.liteloaderloader.core.transformers.compat;

import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.JumpInsnNode;
import org.objectweb.asm.tree.LabelNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;

import com.midnight.liteloaderloader.core.LiteloaderLoader;
import com.midnight.liteloaderloader.core.transformers.ClassTransformer;

public class VoxelMapKeyRepeatTransformer extends ClassTransformer {

    private static final long cushionTime = LiteloaderLoader.voxelMapKeyRepeatBufferTime;
    private static long enterTime = 0L;
    private static boolean enterTrigger = false;

    public VoxelMapKeyRepeatTransformer() {
        super();
        methodTransforms.put("m", this::m);
        methodTransforms.put("a", this::a);
    }

    private void m(MethodNode methodNode) {
        InsnList insert = new InsnList();
        /*
         * com.midnight.liteloaderloader.core.transformers.compat.VoxelMapKeyRepeatTransformer.closeBefore()
         */
        {
            insert.add(
                new MethodInsnNode(
                    Opcodes.INVOKESTATIC,
                    "com/midnight/liteloaderloader/core/transformers/compat/VoxelMapKeyRepeatTransformer",
                    "closeBefore",
                    "()V",
                    false));
        }

        methodNode.instructions.insertBefore(methodNode.instructions.getFirst(), insert);
    }

    private void a(MethodNode method) {
        if ((method.access & Opcodes.ACC_PROTECTED) != 0 && (method.access & Opcodes.ACC_FINAL) != 0
            && method.desc.equals("(CI)V")) {

            InsnList insert = new InsnList();
            /*
             * if
             * (!com.midnight.liteloaderloader.core.transformers.compat.VoxelMapKeyRepeatTransformer.inputBefore())
             * {
             * return;
             * }
             */
            {
                insert.add(
                    new MethodInsnNode(
                        Opcodes.INVOKESTATIC,
                        "com/midnight/liteloaderloader/core/transformers/compat/VoxelMapKeyRepeatTransformer",
                        "inputBefore",
                        "()Z",
                        false));

                LabelNode continueLabel = new LabelNode();
                insert.add(new JumpInsnNode(Opcodes.IFNE, continueLabel));
                insert.add(new InsnNode(Opcodes.RETURN));
                insert.add(continueLabel);
            }

            method.instructions.insertBefore(method.instructions.getFirst(), insert);
        }
    }

    @SuppressWarnings("unused") // called by the injected code
    public static void closeBefore() {
        enterTrigger = false;
    }

    @SuppressWarnings("unused") // called by the injected code
    public static boolean inputBefore() {
        if (!enterTrigger) {
            enterTime = System.nanoTime();
            enterTrigger = true;
        }
        return System.nanoTime() > enterTime + cushionTime;
    }
}
