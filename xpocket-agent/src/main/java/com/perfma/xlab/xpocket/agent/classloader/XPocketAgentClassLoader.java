package com.perfma.xlab.xpocket.agent.classloader;

import java.net.URL;
import java.net.URLClassLoader;

/**
 *
 * @author gongyu <yin.tong@perfma.com>
 */
public class XPocketAgentClassLoader extends URLClassLoader {

    private ClassLoader parent;
    
    public XPocketAgentClassLoader(URL[] urls, ClassLoader cl) {
        super(urls, cl);
    }

    public XPocketAgentClassLoader(URL[] urls) {
        super(urls);
    }

    @Override
    public Class<?> loadClass(String name) throws ClassNotFoundException {
        return loadClass(name, false);
    }

    @Override
    protected Class<?> loadClass(String name, boolean resolve) throws ClassNotFoundException {
        synchronized (getClassLoadingLock(name)) {
            Class<?> c = null;
            
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
