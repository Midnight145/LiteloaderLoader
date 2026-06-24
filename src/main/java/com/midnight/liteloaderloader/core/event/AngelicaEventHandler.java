package com.midnight.liteloaderloader.core.event;

import java.util.function.Consumer;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.EntityRenderer;

import com.gtnewhorizons.angelica.event.ClientEvent;
import com.mumfrey.liteloader.client.CallbackProxyClient;
import com.mumfrey.liteloader.transformers.event.EventInfo;

public class AngelicaEventHandler {

    Consumer<ClientEvent> liteloaderEventConsumer = liteloaderEvent -> {
        if (liteloaderEvent.event == null) return;

        switch (liteloaderEvent.event) {
            case PRE_RENDER_GUI -> CallbackProxyClient.preRenderGUI(
                new EventInfo<>("prerendergui", liteloaderEvent.renderer, false),
                liteloaderEvent.partialTicks);
            case PRE_RENDER_HUD -> CallbackProxyClient.postRenderHUD(
                new EventInfo<>("prerenderhud", liteloaderEvent.renderer, false),
                liteloaderEvent.partialTicks);
            case POST_RENDER_HUD -> CallbackProxyClient.postRenderHUD(
                new EventInfo<>("postrenderhud", liteloaderEvent.renderer, false),
                liteloaderEvent.partialTicks);
            case ON_TICK -> CallbackProxyClient.onTick(new EventInfo<>("ontick", Minecraft.getMinecraft(), false));
        }
    };

    public AngelicaEventHandler() {
        ClientEvent.BUS.addListener(liteloaderEventConsumer);
    }
}
