package com.midnight.liteloaderloader.core;

import org.objectweb.asm.ClassWriter;

public final class SafeClassWriter extends ClassWriter {

    public SafeClassWriter(int flags) {
        super(flags);
    }

    public SafeClassWriter(org.objectweb.asm.ClassReader cr, int flags) {
        super(cr, flags);
    }

    @Override
    protected String getCommonSuperClass(String a, String b) {
        try {
            ClassLoader cl = SafeClassWriter.class.getClassLoader();
            Class<?> c1 = Class.forName(a.replace('/', '.'), false, cl);
            Class<?> c2 = Class.forName(b.replace('/', '.'), false, cl);
            if (c1.isAssignableFrom(c2)) return a;
            if (c2.isAssignableFrom(c1)) return b;
            if (c1.isInterface() || c2.isInterface()) return "java/lang/Object";
            do {
                c1 = c1.getSuperclass();
            } while (!c1.isAssignableFrom(c2));
            return c1.getName()
                .replace('.', '/');
        } catch (Throwable t) {
            return "java/lang/Object";
        }
    }
}
