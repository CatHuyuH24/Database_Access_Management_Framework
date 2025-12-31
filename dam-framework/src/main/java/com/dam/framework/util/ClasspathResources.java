package com.dam.framework.util;

import java.io.InputStream;

public final class ClasspathResources {
    private ClasspathResources() {
        // to hide constructor
    }

    public static InputStream getResourceAsStream(String path) {

        // robust handling to get the class loader
        ClassLoader cl = Thread.currentThread().getContextClassLoader();
        if (cl == null) {
            cl = ClasspathResources.class.getClassLoader();
        }
        return cl.getResourceAsStream(path);
    }
}
