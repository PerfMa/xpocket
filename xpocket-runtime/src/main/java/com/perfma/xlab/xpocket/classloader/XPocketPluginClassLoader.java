package com.perfma.xlab.xpocket.classloader;

import java.net.URL;
import java.net.URLClassLoader;

/**
 * @author gongyu <yin.tong@perfma.com>
 */
public class XPocketPluginClassLoader extends URLClassLoader {

    private ClassLoader parent;

    public XPocketPluginClassLoader(URL[] urls) {
        super(urls);
    }

    public XPocketPluginClassLoader(URL[] urls, ClassLoader parent) {
        super(urls, parent);
    }

    @Override
    public Class<?> loadClass(String name) throws ClassNotFoundException {
        return loadClass(name, false);
    }

    @Override
    protected Class<?> loadClass(String name, boolean resolve) throws ClassNotFoundException {
        synchronized (getClassLoadingLock(name)) {
            Class<?> c = null;

            //do not let plugin classloader load system classes
            if (name.startsWith("com.perfma.xlab.xpocket")) {
                if (parent != null) {
                    c = parent.loadClass(name);
                } else {
                    try {
                        c = Class.forName(name);
                    } catch (Throwable ex) {
                        //ignore it
                    }
                }
            }

            if (c == null) {
                // First, check if the class has already been loaded
                c = findLoadedClass(name);
                try {
                    if (c == null) {
                        c = findClass(name);
                    }
                } catch (ClassNotFoundException e) {
                    // ClassNotFoundException thrown if class not found
                    // from the non-null parent class loader
                }
            }

            if (c == null) {
                if (parent != null) {
                    c = parent.loadClass(name);
                } else {
                    c = Class.forName(name);
                }
            }

            if (resolve) {
                resolveClass(c);
            }

            return c;
        }
    }

}
