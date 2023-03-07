package com.perfma.xlab.xpocket.utils;

import sun.net.www.protocol.file.FileURLConnection;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.JarURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Objects;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

/**
 * @author gongyu <yin.tong@perfma.com>
 */
public class JarUtils {


    public static List<String> findClassesInJarPackage(String packageName)
            throws IOException {

        packageName = packageName.replace(".", "/");
        packageName = packageName.endsWith("/") ? packageName : packageName + "/";

        ClassLoader loader = JarUtils.class.getClassLoader();

        URL url = loader.getResource(packageName);
        List<String> classes = new ArrayList<>();

        URLConnection urlConnection = Objects.requireNonNull(url).openConnection();
        if (urlConnection == null) {
            return classes;
        }

        if (urlConnection instanceof JarURLConnection) {
            findJarPackage(packageName, classes, urlConnection);
        } else if (urlConnection instanceof FileURLConnection) {
            findClassPackage(packageName, classes, url);
        }

        return classes;
    }

    private static void findClassPackage(String packageName, List<String> classes, URL url) throws UnsupportedEncodingException {
        File classFiles = new File(URLDecoder.decode(url.getPath(), "UTF-8"));
        for (File classFile : Objects.requireNonNull(classFiles.listFiles())) {
            if (!classFile.getName().contains("$")) {
                classes.add(packageName.replace("/", ".") + classFile.getName().replace(".class", ""));
            }
        }
    }

    private static void findJarPackage(String packageName, List<String> classes, URLConnection urlConnection) throws IOException {
        JarURLConnection conn = (JarURLConnection) urlConnection;
        JarFile jarFile = conn.getJarFile();
        Enumeration<JarEntry> entries = jarFile.entries();
        while (entries.hasMoreElements()) {
            JarEntry entry = entries.nextElement();
            String name = entry.getName();
            if (!entry.isDirectory() && !name.contains("$")) {
                if (name.substring(0, name.lastIndexOf('/') + 1).equals(packageName)) {
                    classes.add(name.replace(".class", "").replace("/", "."));
                }
            }
        }
    }


}
