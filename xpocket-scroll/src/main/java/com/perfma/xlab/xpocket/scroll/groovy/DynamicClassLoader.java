package com.perfma.xlab.xpocket.scroll.groovy;

import java.net.URL;
import java.net.URLClassLoader;

/**
 *
 * @author gongyu <yin.tong@perfma.com>
 */
public class DynamicClassLoader extends URLClassLoader {

    public DynamicClassLoader(URL[] urls) {
        super(urls);
    }

    public DynamicClassLoader(URL[] urls, ClassLoader cl) {
        super(urls, cl);
    }
    
    public Class<?> loadClass(byte[] b, int off, int len) {
        return defineClass(null, b, off, len);
    }
    
}
