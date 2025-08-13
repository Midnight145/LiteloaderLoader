package com.midnight.liteloaderloader.core.lib;

import java.lang.reflect.Field;
import java.util.LinkedList;

import com.mumfrey.liteloader.client.gui.startup.LoadingBar;
import com.mumfrey.liteloader.core.LiteLoader;
import com.mumfrey.liteloader.core.LiteLoaderMods;

import cpw.mods.fml.common.ProgressManager;

public class LLLLoadingBar extends LoadingBar {

    private static Field modsField;
    private static Field initModsField;

    private static Field stepsField;

    static {
        try {
            modsField = LiteLoader.class.getDeclaredField("mods");
            modsField.setAccessible(true);

            initModsField = LiteLoaderMods.class.getDeclaredField("allMods");
            initModsField.setAccessible(true);

            stepsField = ProgressManager.ProgressBar.class.getDeclaredField("steps");
            stepsField.setAccessible(true);

            try {
                Field modifiers = Field.class.getDeclaredField("modifiers");
                modifiers.setAccessible(true);
                modifiers.setInt(stepsField, stepsField.getModifiers() & ~java.lang.reflect.Modifier.FINAL);
            } catch (NoSuchFieldException | IllegalAccessException e) {
                // If the modifiers field doesn't exist, we can ignore it. Java 9+ removed it.
            }
        } catch (NoSuchFieldException e) {
            throw new RuntimeException(e);
        }
    }

    private String message = "";
    private int modCount = 0;
    private int stepped = 0;

    public ProgressManager.ProgressBar progressBar = null;

    protected void _setEnabled(boolean enabled) {

    }

    protected void _dispose() {

    }

    protected void _incLiteLoaderProgress() {
        this._incLiteLoaderProgress(this.message);
    }

    protected void _setMessage(String message) {
        this.message = message;
    }

    protected void _incLiteLoaderProgress(String message) {
        this.message = message;

        if (message.startsWith("Initialising mod")) {
            if (progressBar == null) {
                this.modCount = this.getModCount();
                progressBar = ProgressManager.push("LiteLoader: Initializing", this.modCount, false);
            }
            if (this.getModCount() != this.modCount) {
                this.setSteps(progressBar, this.getModCount());
                this.modCount = this.getModCount();
            }
            progressBar.step(formatMessage(message));
            stepped++;
            if (stepped >= this.modCount) {
                ProgressManager.pop(progressBar);
                progressBar = null;
            }
        }
    }

    protected void _incTotalLiteLoaderProgress(int by) {

    }

    private int getModCount() {
        try {
            return ((LinkedList) initModsField.get(modsField.get(LiteLoader.getInstance()))).size();
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    private String formatMessage(String message) {
        String formattedMessage = message;
        String regex = "Initialising mod (.+) version.+";
        if (formattedMessage.matches(regex)) {
            formattedMessage = formattedMessage.replaceAll(regex, "$1");
        }
        return formattedMessage;
    }

    private void setSteps(ProgressManager.ProgressBar progressBar, int steps) {
        try {
            stepsField.set(progressBar, steps);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }
}
