package com.midnight.liteloaderloader.core.lib;

import com.mumfrey.liteloader.core.LiteLoader;
import com.mumfrey.liteloader.core.LiteLoaderMods;
import cpw.mods.fml.common.ProgressManager;

import java.lang.reflect.Field;

public class ReflectionHelper {
    public static final Field LiteLoader$modsField;
    public static final Field ProgressManager$ProgressBar$stepsField;
    static {
        try {
            LiteLoader$modsField = LiteLoader.class.getDeclaredField("mods");
            LiteLoader$modsField.setAccessible(true);

            //noinspection deprecation
            ProgressManager$ProgressBar$stepsField = ProgressManager.ProgressBar.class.getDeclaredField("steps");
            ProgressManager$ProgressBar$stepsField.setAccessible(true);

            try {
                Field modifiers = Field.class.getDeclaredField("modifiers");
                modifiers.setAccessible(true);
                modifiers.setInt(ProgressManager$ProgressBar$stepsField, ProgressManager$ProgressBar$stepsField.getModifiers() & ~java.lang.reflect.Modifier.FINAL);
            } catch (NoSuchFieldException | IllegalAccessException e) {
                // If the modifiers field doesn't exist, we can ignore it. Java 9+ removed it.
            }
        } catch (NoSuchFieldException e) {
            throw new RuntimeException(e);
        }
    }

    public static LiteLoaderMods getLiteLoaderMods() {
        try {
            return (LiteLoaderMods) LiteLoader$modsField.get(LiteLoader.getInstance());
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    @SuppressWarnings("deprecation")
    public static void setProgressBarSteps(ProgressManager.ProgressBar bar, int steps) {
        try {
            ProgressManager$ProgressBar$stepsField.set(bar, steps);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

}
