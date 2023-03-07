package com.perfma.xlab.xpocket.utils;

import com.perfma.xlab.xpocket.plugin.adaptor.JavaAgentCommandAdaptor;
import jline.internal.Configuration;

import java.io.File;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.regex.Pattern;

/**
 * @author gongyu <yin.tong@perfma.com>
 */
public class XPocketConstants {

    public static final JavaAgentCommandAdaptor DEFAULT_ADAPTOR = new JavaAgentCommandAdaptor();

    public static final String JAVA_HOME = System.getProperty("java.home");

    public static final String SYSTEM_PLUGIN_NAME = "system";

    public static final String SYSTEM_PLUGIN_NS = "XPOCKET";

    public static final String USER_HOME = System.getProperty("user.home");

    public static final String PATH = USER_HOME + File.separator + ".xpocket" + File.separator + ".history" + File.separator;

    public static final String XPOCKET_COMMAND_PACKAGE = "com.perfma.xlab.xpocket.command.impl";

    public static final String VERSION = "2.1.2-alpha";

    public static final String GITHUB = "https://github.com/PerfMa/xpocket";

    public static final String CLUB = "https://xpocket.perfma.com/";

    public static final String LOCAL_USER_HOME = System.getProperty("user.dir") + File.separator + "xpocket-deploy";

    public static final String PLUGIN_PATH = "XPOCKET_PLUGIN_PATH";

    public static final String CONFIG_PATH = "XPOCKET_CONFIG_PATH";

    public static final String SCROLL_PATH = "XPOCKET_SCROLL_PATH";

    public static final String HOME_NAME = "XPOCKET_HOME";

    public static final String PLUGINS_DIR = "plugins";

    public static final String CONFIG_DIR = "config";

    public static final String TOOLS_DIR = "tools";

    public static final String LOCAL_TOOLS_JAR_NAME = "tools.jar";

    public static final String MAC = "macosx";

    public static final String WINDOWS = "windows";

    public static final String LINUX = "linux";

    public static final Double JDK_8 = 1.8;

    private XPocketConstants() {
    }

    public static void localInit() {
        initSystemProperties();
        initLocalRun();
    }

    public static void initLocalRun() {
        String jdkVersion = System.getProperty("java.specification.version");
        if (Double.parseDouble(jdkVersion) > JDK_8) {
            return;
        }

        String localToolsFolder = WINDOWS;
        if (Configuration.isMac()) {
            localToolsFolder = MAC;
        } else if (Configuration.isLinux()) {
            localToolsFolder = LINUX;
        }

        String expr = ".*";
        boolean isLocal = Pattern.matches(expr + localToolsFolder + expr + LOCAL_TOOLS_JAR_NAME + expr, System.getProperty("java.class.path"));

        if (!isLocal) {
            loadToolsJar(localToolsFolder);
        }
    }

    private static void loadToolsJar(String localToolsFolder) {
        String localToolsPath = LOCAL_USER_HOME + File.separator + TOOLS_DIR + File.separator + localToolsFolder + File.separator + LOCAL_TOOLS_JAR_NAME;
        File localToolsFile = new File(localToolsPath);

        try {
            URLClassLoader urlClassLoader = (URLClassLoader) XPocketConstants.class.getClassLoader();
            Method addUrl = URLClassLoader.class.getDeclaredMethod("addURL", URL.class);
            addUrl.setAccessible(true);
            addUrl.invoke(urlClassLoader, localToolsFile.toURI().toURL());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void initSystemProperties() {
        if (System.getProperty(HOME_NAME) == null) {
            System.setProperty(HOME_NAME, LOCAL_USER_HOME);
        }
        if (System.getProperty(PLUGIN_PATH) == null) {
            System.setProperty(PLUGIN_PATH, LOCAL_USER_HOME + File.separator + PLUGINS_DIR);
        }
        if (System.getProperty(CONFIG_PATH) == null) {
            System.setProperty(CONFIG_PATH, LOCAL_USER_HOME + File.separator + CONFIG_DIR + File.separator);
        }
    }

    public static String getXpocketScrollPath() {
        return System.getProperty(SCROLL_PATH, System.getProperty(HOME_NAME) + File.separator + "scrolls" + File.separator);
    }

}
