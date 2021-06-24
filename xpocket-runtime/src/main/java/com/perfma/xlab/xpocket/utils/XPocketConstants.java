package com.perfma.xlab.xpocket.utils;

import com.perfma.xlab.xpocket.plugin.adaptor.JavaAgentCommandAdaptor;
import java.io.File;

/**
 * @author gongyu <yin.tong@perfma.com>
 */
public class XPocketConstants {

    public static final JavaAgentCommandAdaptor DEFAULT_ADAPTOR = new JavaAgentCommandAdaptor();
    
    public static final String SYSTEM_PLUGIN_NAME = "system";

    public static final String SYSTEM_PLUGIN_NS = "XPOCKET";

    public static final String USER_HOME = System.getProperty("user.home");

    public static final String PATH = USER_HOME + File.separator + ".xpocket" + File.separator + ".history" + File.separator;

    public static final String[] XPOCKET_COMMANDS = {"Echo","Attach","Detach","History", "Cd", "Help", "ListPlugins", "JPS", "Quit", "Session", "Sys", "Top", "Use", "Split", 
        "Trim", "Grep", "Version", "Clear"};

    public static final String XPOCKET_COMMAND_PACKAGE = "com.perfma.xlab.xpocket.command.impl.";

    public static final String VERSION = "2.1.0-RELEASE";

    public static final String GITHUB = "https://github.com/PerfMa/xpocket";

    public static final String CLUB = "https://xpocket.perfma.com/";

}
