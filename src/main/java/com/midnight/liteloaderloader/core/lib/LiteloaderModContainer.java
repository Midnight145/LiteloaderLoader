package com.midnight.liteloaderloader.core.lib;

import java.io.File;
import java.security.cert.Certificate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.google.common.eventbus.EventBus;
import com.mumfrey.liteloader.core.LiteLoaderMods;
import com.mumfrey.liteloader.core.ModInfo;

import cpw.mods.fml.common.LoadController;
import cpw.mods.fml.common.MetadataCollection;
import cpw.mods.fml.common.ModContainer;
import cpw.mods.fml.common.ModMetadata;
import cpw.mods.fml.common.versioning.ArtifactVersion;
import cpw.mods.fml.common.versioning.VersionRange;

public class LiteloaderModContainer implements ModContainer {

    private static List<LiteloaderModContainer> containers = null;

    private final ModInfo<?> mod;
    private final ModMetadata metadata;

    public LiteloaderModContainer(ModInfo<?> mod) {
        this.mod = mod;
        this.metadata = new ModMetadata();
        this.metadata.modId = mod.getModClassName();
        this.metadata.name = mod.getDisplayName();
        this.metadata.version = mod.getVersion();
        this.metadata.authorList = new ArrayList<>();
        this.metadata.authorList.add(mod.getAuthor());
        this.metadata.description = mod.getDescription();
        this.metadata.url = mod.getURL();
    }

    @SuppressWarnings("unused") // called via asm
    public static void populate(ArrayList<ModContainer> list) {
        if (containers == null) {
            initModlist();
        }
        list.addAll(containers);
    }

    public static void initModlist() {
        containers = new ArrayList<>();
        LiteLoaderMods llmods = ReflectionHelper.getLiteLoaderMods();
        for (ModInfo<?> mod : llmods.getAllMods()) {
            containers.add(new LiteloaderModContainer(mod));
        }
    }

    @Override
    public String getModId() {
        return this.metadata.name.replace(" ", "_");
    }

    @Override
    public String getName() {
        return this.metadata.name;
    }

    @Override
    public String getVersion() {
        return mod.getVersion();
    }

    @Override
    public File getSource() {
        return mod.getContainer()
            .toFile();
    }

    @Override
    public ModMetadata getMetadata() {
        return this.metadata;
    }

    @Override
    public void bindMetadata(MetadataCollection mc) {

    }

    @Override
    public void setEnabledState(boolean enabled) {

    }

    @Override
    public Set<ArtifactVersion> getRequirements() {
        return null;
    }

    @Override
    public List<ArtifactVersion> getDependencies() {
        return null;
    }

    @Override
    public List<ArtifactVersion> getDependants() {
        return null;
    }

    @Override
    public String getSortingRules() {
        return "";
    }

    @Override
    public boolean registerBus(EventBus bus, LoadController controller) {
        return false;
    }

    @Override
    public boolean matches(Object mod) {
        return false;
    }

    @Override
    public Object getMod() {
        return null;
    }

    @Override
    public ArtifactVersion getProcessedVersion() {
        return null;
    }

    @Override
    public boolean isImmutable() {
        return false;
    }

    @Override
    public String getDisplayVersion() {
        return mod.getVersion();
    }

    @Override
    public VersionRange acceptableMinecraftVersionRange() {
        return null;
    }

    @Override
    public Certificate getSigningCertificate() {
        return null;
    }

    @Override
    public Map<String, String> getCustomModProperties() {
        return null;
    }

    @Override
    public Class<?> getCustomResourcePackClass() {
        return null;
    }

    @Override
    public Map<String, String> getSharedModDescriptor() {
        return null;
    }

    @Override
    public Disableable canBeDisabled() {
        return Disableable.RESTART;
    }

    @Override
    public String getGuiClassName() {
        return "";
    }

    @Override
    public List<String> getOwnedPackages() {
        return null;
    }
}
