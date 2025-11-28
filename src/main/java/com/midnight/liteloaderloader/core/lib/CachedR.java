// Code adapted from the original R lib here under the CC0 1.0 Universal license: https://github.com/MrNavaStar/R

package com.midnight.liteloaderloader.core.lib;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.Collections;
import java.util.Map;
import java.util.Objects;
import java.util.WeakHashMap;
import java.util.concurrent.ConcurrentHashMap;

public class CachedR {

    private static final Map<Class<?>, ConcurrentHashMap<String, Field>> FIELDS = Collections
        .synchronizedMap(new WeakHashMap<>());
    private static final Map<Class<?>, ConcurrentHashMap<MethodInfo, Method>> METHODS = Collections
        .synchronizedMap(new WeakHashMap<>());

    private static boolean oldJavaCompat = false;
    static {
        try {
            Field.class.getDeclaredField("modifiers");
            oldJavaCompat = true;
        } catch (NoSuchFieldException ignore) {}
    }

    private final Object instance;
    private final Class<?> clazz;

    public CachedR(Object instance) {
        this.instance = instance;
        clazz = instance.getClass();
    }

    public CachedR(Class<?> clazz) {
        instance = null;
        this.clazz = clazz;
    }

    /**
     * Create an instance of {@link CachedR}. Can be used for static or non-static actions
     */
    public static CachedR of(Object instance) {
        return new CachedR(instance);
    }

    /**
     * Create an instance of {@link CachedR} that can only be used for static actions
     */
    public static CachedR of(Class<?> clazz) {
        return new CachedR(clazz);
    }

    /**
     * Create an instance of {@link CachedR} from a field in another {@link CachedR} instance
     */
    public CachedR of(String name) {
        try {
            return CachedR.of(findField(name, clazz).get(instance));
        } catch (IllegalAccessException | NoSuchFieldException e) {
            throw new RuntimeException(e);
        }
    }

    // Search super classes for field
    private Field findField(String name, Class<?> clazz) throws NoSuchFieldException {
        if (clazz == null) throw new NoSuchFieldException();

        ConcurrentHashMap<String, Field> classCache;

        // WeakHashMap isn't thread safe, but we need it to avoid strong Class references
        synchronized (FIELDS) {
            classCache = FIELDS.get(clazz);
            if (classCache == null) {
                classCache = new ConcurrentHashMap<>();
                FIELDS.put(clazz, classCache);
            }
        }
        Field cached = classCache.get(name);
        if (cached != null) return cached;

        Field field;
        try {
            field = clazz.getDeclaredField(name);
        } catch (NoSuchFieldException e) {
            field = findField(name, clazz.getSuperclass());
        }
        field.setAccessible(true);
        classCache.put(name, field);
        return field;
    }

    // Search super classes for methods
    private Method findMethod(String name, Class<?> clazz, Class<?>[] argTypes) throws NoSuchMethodException {
        if (clazz == null) throw new NoSuchMethodException();

        MethodInfo key = new MethodInfo(name, argTypes);
        ConcurrentHashMap<MethodInfo, Method> classCache;

        synchronized (METHODS) {
            classCache = METHODS.get(clazz);
            if (classCache == null) {
                classCache = new ConcurrentHashMap<>();
                METHODS.put(clazz, classCache);
            }
        }
        Method cached = classCache.get(key);
        if (cached != null) return cached;

        Method method;
        try {
            method = clazz.getDeclaredMethod(name, argTypes);
        } catch (NoSuchMethodException e) {
            method = findMethod(name, clazz.getSuperclass(), argTypes);
        }
        method.setAccessible(true);
        classCache.put(key, method);
        return method;
    }

    /**
     * Get the value of a field. Can be private or static
     */
    public <T> T get(String name, Class<T> type) {
        try {
            return type.cast(findField(name, clazz).get(instance));
        } catch (IllegalAccessException | NoSuchFieldException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Get the value of a field, or return a default value if it fails. Can be private or static
     */
    public <T> T getOrElse(String name, Class<T> type, T orElse) {
        try {
            return type.cast(findField(name, clazz).get(instance));
        } catch (IllegalAccessException | NoSuchFieldException e) {
            return orElse;
        }
    }

    /**
     * Set the value of a field. Can be private, final, or static
     */
    public CachedR set(String name, Object value) {
        try {
            Field toSet = findField(name, clazz);
            if (oldJavaCompat) {
                Field modifiersField = findField("modifiers", toSet.getClass());
                modifiersField.setInt(toSet, toSet.getModifiers() & ~java.lang.reflect.Modifier.FINAL);
            }
            toSet.set(instance, value);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
        return this;
    }

    /**
     * Invoke a function with a return type
     */
    public <T> T call(String name, Class<T> returnType, Object... args) {
        try {
            Class<?>[] classes = Arrays.stream(args)
                .map(Object::getClass)
                .toArray(Class[]::new);
            Object returnVal = findMethod(name, clazz, classes).invoke(instance, args);
            if (returnVal == null || returnType == null) return null;
            return returnType.cast(returnVal);
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Invoke a function with no return type
     */
    public CachedR call(String name, Object... args) {
        call(name, null, args);
        return this;
    }

    /**
     * Get a list of the generic type params of a class
     */
    public Class<?>[] generics() {
        if (clazz.isEnum()) return new Class[] {}; // Enums cant have generics

        Type generic = clazz.getGenericSuperclass();
        if (generic instanceof ParameterizedType) {
            return Arrays.stream(((ParameterizedType) generic).getActualTypeArguments())
                .map(t -> {
                    try {
                        return Class.forName(t.getTypeName());
                    } catch (ClassNotFoundException e) {
                        return null;
                    }
                })
                .filter(Objects::nonNull)
                .toArray(Class[]::new);
        }
        return new Class[] {};
    }
}

// Helper class for caching method lookups
class MethodInfo {
    public final String name;
    public final Class<?>[] argTypes;

    public MethodInfo(String name, Class<?>... argTypes) {
        this.name = name;
        this.argTypes = argTypes;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MethodInfo that = (MethodInfo) o;
        return name.equals(that.name) && Arrays.equals(argTypes, that.argTypes);
    }

    @Override
    public int hashCode() {
        int result = Objects.hash(name);
        result = 31 * result + Arrays.hashCode(argTypes);
        return result;
    }
}
