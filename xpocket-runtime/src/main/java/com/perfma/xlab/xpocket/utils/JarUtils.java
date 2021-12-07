package com.perfma.xlab.xpocket.utils;

import java.io.IOException;
import java.net.JarURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

/**
 *
 * @author gongyu <yin.tong@perfma.com>
 */
public class JarUtils {

    
    public static List<String> findClassesInJarPackage(String packageName) 
            throws IOException {
        
        packageName = packageName.replace(".", "/");
        packageName = packageName.endsWith("/") ? packageName : packageName + "/";
        
        ClassLoader loader = JarUtils.class.getClassLoader();
        
        URL url = loader.getResource(packageName);
        
        JarURLConnection conn = (JarURLConnection)url.openConnection();
        JarFile jarFile = conn.getJarFile();
        Enumeration<JarEntry> entries = jarFile.entries();
        List<String>  classes = new ArrayList<>();
        while(entries.hasMoreElements()) {
            JarEntry entry = entries.nextElement();
            String name = entry.getName();
            if(!entry.isDirectory() && !name.contains("$") ) {
                if(name.substring(0, name.lastIndexOf('/') + 1).equals(packageName)) {
                    classes.add(name.replace(".class", "").replace("/", "."));
                }
            }   
        }
        
        return classes;
    }
    
    public static void main(String[] args) throws IOException {
        
        List<String> classes = JarUtils.findClassesInJarPackage("org.apache.commons.io");
        for(String clazz : classes) {
            System.out.println(clazz);
        }  
    }
    
}
