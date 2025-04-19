package com.midnight.liteloaderloader.lib;

public class StringUtil {

    public static String getShortClassName(Class clazz) {
        String className = clazz.getSimpleName();
        int dollarIndex = className.lastIndexOf("$");
        if (dollarIndex != -1) {
            return className.substring(dollarIndex + 1);
        } else {
            return className;
        }
    }
}
