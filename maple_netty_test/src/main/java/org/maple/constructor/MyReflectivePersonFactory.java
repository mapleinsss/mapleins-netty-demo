package org.maple.constructor;

import io.netty.channel.ReflectiveChannelFactory;
import io.netty.util.internal.ObjectUtil;

import java.lang.reflect.Constructor;

import static io.netty.util.internal.ObjectUtil.checkNotNull;

public class MyReflectivePersonFactory<T extends Person> {

    private final Constructor<? extends T> constructor;
    private static final char PACKAGE_SEPARATOR_CHAR = '.';

    public MyReflectivePersonFactory(Class<? extends T> clazz) {
        ObjectUtil.checkNotNull(clazz, "clazz");
        try {
            this.constructor = clazz.getConstructor();
        } catch (NoSuchMethodException e) {
            throw new IllegalArgumentException("Class " + simpleClassName(clazz) +
                    " does not have a public non-arg constructor", e);
        }
    }

    public T newPerson() {
        try {
            return constructor.newInstance();
        } catch (Throwable t) {
            throw new RuntimeException("Unable to create Person from class " + constructor.getDeclaringClass(), t);
        }
    }

    @Override
    public String toString() {
        return simpleClassName(ReflectiveChannelFactory.class) +
                '(' + simpleClassName(constructor.getDeclaringClass()) + ".class)";
    }

    public static String simpleClassName(Class<?> clazz) {
        String className = checkNotNull(clazz, "clazz").getName();
        final int lastDotIdx = className.lastIndexOf(PACKAGE_SEPARATOR_CHAR);
        if (lastDotIdx > -1) {
            return className.substring(lastDotIdx + 1);
        }
        return className;
    }
}
