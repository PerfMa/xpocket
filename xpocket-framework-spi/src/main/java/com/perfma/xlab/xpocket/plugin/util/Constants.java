package com.perfma.xlab.xpocket.plugin.util;

import java.io.File;

/**
 * @author gongyu <yin.tong@perfma.com>
 */
public class Constants {

    public static final String DEFAULT_RUN_MODE = "DEFAULT";

    public static final String RUN_MODE_KEY = "run_mode";

    public static final String AGENT_MODE_KEY = "agent_mode";

    public static final String XPOCKET_SIMPLE_MODE = "XPOCKET_SIMPLE_MODE";

    public static final String USER_HOME = System.getProperty("user.dir") + File.separator + "xpocket-deploy";

    public static final String HOME_NAME = "XPOCKET_HOME";

    public static final String PLUGIN_PATH = "XPOCKET_PLUGIN_PATH";

    public static final String CONFIG_PATH = "XPOCKET_CONFIG_PATH";

    public static final String PLUGINS_DIR = "plugins";

    public static final String CONFIG_DIR = "config";

    private Constants() {
    }

    public static void initSystemProperties() {
        if (System.getProperty(HOME_NAME) == null) {
            System.setProperty(HOME_NAME, USER_HOME);
        }
        if (System.getProperty(PLUGIN_PATH) == null) {
            System.setProperty(PLUGIN_PATH, USER_HOME + File.separator + PLUGINS_DIR);
        }
        if (System.getProperty(CONFIG_PATH) == null) {
            System.setProperty(CONFIG_PATH, USER_HOME + File.separator + CONFIG_DIR + File.separator);
        }
    }
}
