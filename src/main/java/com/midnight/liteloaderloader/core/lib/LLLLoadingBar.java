package com.midnight.liteloaderloader.core.lib;

import com.mumfrey.liteloader.client.gui.startup.LoadingBar;
import com.mumfrey.liteloader.core.LiteLoaderMods;

import cpw.mods.fml.common.ProgressManager;

@SuppressWarnings({"unused", "deprecation"}) // called via ASM
public class LLLLoadingBar extends LoadingBar {

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
            int newModCount = this.getModCount();
            if (newModCount != this.modCount) {
                ReflectionHelper.setProgressBarSteps(progressBar, newModCount);
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
        LiteLoaderMods mods = ReflectionHelper.getLiteLoaderMods();
        return mods.getAllMods().size();
    }

    private String formatMessage(String message) {
        String formattedMessage = message;
        String regex = "Initialising mod (.+) version.+";
        if (formattedMessage.matches(regex)) {
            formattedMessage = formattedMessage.replaceAll(regex, "$1");
        }
        return formattedMessage;
    }
}
