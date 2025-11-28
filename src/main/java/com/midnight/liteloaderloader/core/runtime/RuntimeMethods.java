package com.midnight.liteloaderloader.core.runtime;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiControls;
import net.minecraft.client.gui.GuiKeyBindingList;
import net.minecraft.client.gui.GuiListExtended;
import net.minecraft.client.gui.GuiScreen;

import com.blamejared.controlling.client.gui.GuiNewControls;
import com.midnight.liteloaderloader.core.lib.CachedR;

import cpw.mods.fml.common.Loader;

// Class containing code that runs after modloading is finished so we don't have to worry about classloading issues.
// Methods here are called via bytecode injection from transformers.
public class RuntimeMethods {

    private static final boolean controllingLoaded = Loader.isModLoaded("controlling");

    public static GuiListExtended.IGuiListEntry[] getKeyBindingEntries(GuiKeyBindingList dummy) {
        // called by injected code in transformers.compat.MacroModCoreTransformer
        GuiScreen realScreen = Minecraft.getMinecraft().currentScreen;
        if (!(realScreen instanceof GuiControls controls)) {
            return new GuiListExtended.IGuiListEntry[0];
        }
        if (!controllingLoaded) {
            return controls.keyBindingList.field_148190_m;
        }
        GuiNewControls newControls = (GuiNewControls) controls;
        // noinspection unchecked
        List<GuiListExtended.IGuiListEntry> entries = CachedR.of(newControls)
            .of("guiNewKeyBindingList")
            .getOrElse("displayedEntries", List.class, new ArrayList<>());
        return entries.toArray(new GuiListExtended.IGuiListEntry[0]);
    }
}
