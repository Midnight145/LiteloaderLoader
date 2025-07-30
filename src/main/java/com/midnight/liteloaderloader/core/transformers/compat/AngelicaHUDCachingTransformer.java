package com.midnight.liteloaderloader.core.transformers.compat;

import static org.spongepowered.asm.lib.Opcodes.ALOAD;
import static org.spongepowered.asm.lib.Opcodes.FLOAD;
import static org.spongepowered.asm.lib.Opcodes.INVOKESTATIC;

import net.minecraft.client.renderer.EntityRenderer;

import org.spongepowered.asm.lib.Opcodes;
import org.spongepowered.asm.lib.tree.AbstractInsnNode;
import org.spongepowered.asm.lib.tree.InsnList;
import org.spongepowered.asm.lib.tree.MethodInsnNode;
import org.spongepowered.asm.lib.tree.MethodNode;
import org.spongepowered.asm.lib.tree.VarInsnNode;

import com.midnight.liteloaderloader.core.transformers.ClassTransformer;
import com.mumfrey.liteloader.client.CallbackProxyClient;
import com.mumfrey.liteloader.transformers.event.EventInfo;

public class AngelicaHUDCachingTransformer extends ClassTransformer {

    public AngelicaHUDCachingTransformer() {
        super();
        methodTransforms.put("renderCachedHud", this::injectIntoRenderCachedHud);
    }

    private void injectIntoRenderCachedHud(MethodNode methodNode) {
        InsnList list = callFunction("throwPreEvents");
        AbstractInsnNode first = methodNode.instructions.getFirst();
        methodNode.instructions.insertBefore(first, list);

        list = callFunction("throwPostEvents");
        AbstractInsnNode last;
        // noinspection StatementWithEmptyBody
        for (last = methodNode.instructions.getLast(); last.getOpcode() != Opcodes.RETURN; last = last.getPrevious());
        methodNode.instructions.insertBefore(last, list);
    }

    private static InsnList callFunction(String methodName) {
        // AngelicaHUDCachingTransformer.methodName(EntityRenderer, float)
        InsnList list = new InsnList();
        // ALOAD 0 (EntityRenderer object)
        list.add(new VarInsnNode(ALOAD, 0));
        // FLOAD 2 (partialTicks float)
        list.add(new VarInsnNode(FLOAD, 2));
        // actually call the method
        list.add(
            new MethodInsnNode(
                INVOKESTATIC,
                "com/midnight/liteloaderloader/core/transformers/compat/AngelicaHUDCachingTransformer",
                methodName,
                "(Lnet/minecraft/client/renderer/EntityRenderer;F)V",
                false));
        return list;
    }

    @SuppressWarnings("unused") // called by the injected code
    public static void throwPreEvents(EntityRenderer renderer, float partialTicks) {
        CallbackProxyClient.preRenderGUI(new EventInfo<>("prerendergui", renderer, false), partialTicks);
        CallbackProxyClient.postRenderHUD(new EventInfo<>("prerenderhud", renderer, false), partialTicks);
    }

    @SuppressWarnings("unused") // called by the injected code
    public static void throwPostEvents(EntityRenderer renderer, float partialTicks) {
        CallbackProxyClient.postRenderHUD(new EventInfo<>("postrenderhud", renderer, false), partialTicks);
    }
}
