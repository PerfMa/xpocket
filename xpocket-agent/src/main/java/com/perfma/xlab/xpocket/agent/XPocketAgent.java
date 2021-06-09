package com.perfma.xlab.xpocket.agent;

import com.perfma.xlab.xpocket.agent.classloader.XPocketAgentClassLoader;
import java.io.File;
import java.lang.instrument.Instrumentation;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLDecoder;
import java.util.Properties;

/**
 *
 * @author gongyu <yin.tong@perfma.com>
 */
public class XPocketAgent {

    private static final String FILE_SEPARATOR
            = File.separator;

    private static final String JAVA_VERSION
            = System.getProperty("java.specification.version");

    private static final String OS_NAME = System.getProperty("os.name");

    private static final String MAIN_LAUNCHER
            = "com.perfma.xlab.xpocket.framework.spi.impl.agent"
            + ".XPocketAgentLauncher";

    public static void premain(String agentArgs, Instrumentation inst) {
        doInit(agentArgs, inst, true);
    }

    public static void agentmain(String agentArgs, Instrumentation inst) {
        doInit(agentArgs, inst, false);
    }

    private static void doInit(String agentArgs, Instrumentation inst,
            boolean isOnLoad) {

        try {

            Properties args = new Properties();

            String[] paramPairs = agentArgs.split(";");
            for (String paramPair : paramPairs) {
                String[] param = paramPair.split("=");
                args.setProperty(param[0], URLDecoder.decode(param[1], "UTF-8"));
            }
            String xpocketHome = args.getProperty("XPOCKET_HOME");

            checkLibraryPath(xpocketHome);
            File libDir = new File(xpocketHome + FILE_SEPARATOR + "lib");
            File[] files = libDir.listFiles();
            URL[] libCp;

            String toolsPath = getToolsPath(xpocketHome);
            if (toolsPath != null) {
                libCp = new URL[files.length + 2];
                libCp[0] = libDir.toURI().toURL();
                libCp[libCp.length - 1] = new File(toolsPath).toURI().toURL();
            } else {
                libCp = new URL[files.length + 1];
                libCp[0] = libDir.toURI().toURL();
            }

            for (int i = 0; i < files.length; i++) {
                libCp[i + 1] = files[i].toURI().toURL();
            }
            ClassLoader classloader = new XPocketAgentClassLoader(libCp,
                    XPocketAgent.class.getClassLoader());
            Class launcherClass = classloader
                    .loadClass(MAIN_LAUNCHER);
            Method startMethod = launcherClass.getMethod("start",
                    Properties.class, Instrumentation.class, boolean.class);
            startMethod.invoke(null, args, inst, isOnLoad);
        } catch (Throwable ex) {
            ex.printStackTrace();
        }

    }

    private static void checkLibraryPath(String xpocketHome) {
        String originalPath = System.getProperty("java.library.path");
        String xpocketLibPath = getLibPath(xpocketHome);
        if (originalPath != null && xpocketLibPath != null) {
            System.setProperty("java.library.path",
                    originalPath + File.pathSeparator + xpocketLibPath);
        } else if (xpocketLibPath != null) {
            System.setProperty("java.library.path", xpocketLibPath);
        }
    }

    private static String getLibPath(String xpocketHome) {
        if (!JAVA_VERSION.startsWith("1.")) {
            return null;
        }

        if (OS_NAME.toLowerCase().contains("win")) {
            return xpocketHome + FILE_SEPARATOR + "tools" + FILE_SEPARATOR
                    + "windows" + FILE_SEPARATOR;
        } else if (OS_NAME.toLowerCase().contains("mac")) {
            return xpocketHome + FILE_SEPARATOR + "tools" + FILE_SEPARATOR
                    + "macosx" + FILE_SEPARATOR;
        } else if (OS_NAME.toLowerCase().contains("nix")
                || OS_NAME.toLowerCase().contains("nux")
                || OS_NAME.toLowerCase().contains("aix")) {
            return xpocketHome + FILE_SEPARATOR + "tools" + FILE_SEPARATOR
                    + "linux" + FILE_SEPARATOR;
        }

        return null;
    }

    private static String getToolsPath(String xpocketHome) {
        if (!JAVA_VERSION.startsWith("1.")) {
            return null;
        }

        if (OS_NAME.toLowerCase().contains("win")) {
            return xpocketHome + FILE_SEPARATOR + "tools" + FILE_SEPARATOR
                    + "windows" + FILE_SEPARATOR + "tools.jar";
        } else if (OS_NAME.toLowerCase().contains("mac")) {
            return xpocketHome + FILE_SEPARATOR + "tools" + FILE_SEPARATOR
                    + "macosx" + FILE_SEPARATOR + "tools.jar";
        } else if (OS_NAME.toLowerCase().contains("nix")
                || OS_NAME.toLowerCase().contains("nux")
                || OS_NAME.toLowerCase().contains("aix")) {
            return xpocketHome + FILE_SEPARATOR + "tools" + FILE_SEPARATOR
                    + "linux" + FILE_SEPARATOR + "tools.jar";
        }

        return null;
    }

}
