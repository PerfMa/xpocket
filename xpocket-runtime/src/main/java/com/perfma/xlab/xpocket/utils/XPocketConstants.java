package com.perfma.xlab.xpocket.utils;

import com.perfma.xlab.xpocket.plugin.adaptor.JavaAgentCommandAdaptor;
import java.io.File;

/**
 * @author gongyu <yin.tong@perfma.com>
 */
public class XPocketConstants {

    public static final JavaAgentCommandAdaptor DEFAULT_ADAPTOR = new JavaAgentCommandAdaptor();
    
    public static final String JAVA_HOME = System.getProperty("java.home");
    
    public static final String XPOCKET_HOME = System.getProperty("XPOCKET_HOME");

    public static final String XPOCKET_PLUGIN_PATH = System.getProperty("XPOCKET_PLUGIN_PATH",XPOCKET_HOME+ "/plugins/");

    public static final String XPOCKET_CONFIG_PATH = System.getProperty("XPOCKET_CONFIG_PATH",XPOCKET_HOME+ "/config/");
    
    public static final String XPOCKET_SCROLL_PATH = System.getProperty("XPOCKET_SCROLL_PATH",XPOCKET_HOME+ "/scrolls/");
    
    public static final String SYSTEM_PLUGIN_NAME = "system";

    public static final String SYSTEM_PLUGIN_NS = "XPOCKET";

    public static final String USER_HOME = System.getProperty("user.home");

    public static final String PATH = USER_HOME + File.separator + ".xpocket" + File.separator + ".history" + File.separator;

    public static final String[] XPOCKET_COMMANDS = {"Echo","Attach","Detach","History", "Cd", "Help", "ListPlugins", "JPS", "Quit", "Session", "Sys", "Top", "Use", "Split", 
        "Trim", "Grep", "Version", "Clear","Kill"};

    public static final String XPOCKET_COMMAND_PACKAGE = "com.perfma.xlab.xpocket.command.impl";

    public static final String VERSION = "2.1.2-alpha";

    public static final String GITHUB = "https://github.com/PerfMa/xpocket";

    public static final String CLUB = "https://xpocket.perfma.com/";

}
