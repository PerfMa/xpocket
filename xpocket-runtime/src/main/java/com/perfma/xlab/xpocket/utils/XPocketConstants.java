package com.perfma.xlab.xpocket.utils;

import java.io.File;

/**
 * @author gongyu <yin.tong@perfma.com>
 */
public class XPocketConstants {

    public static final String SYSTEM_PLUGIN_NAME = "system";

    public static final String SYSTEM_PLUGIN_NS = "XPOCKET";

    public static final String USER_HOME = System.getProperty("user.home");

    public static final String PATH = USER_HOME + File.separator + ".xpocket" + File.separator + ".history" + File.separator;

    public static final String[] XPOCKET_COMMANDS = {"History", "Cd", "Help", "ListPlugins", "JPS","Quit", "Session", "Sys", "Top", "Use", "Split", "Trim", "Grep", "Version", "Clear"};

    public static final String XPOCKET_COMMAND_PACKAGE = "com.perfma.xlab.xpocket.command.impl.";

    public static final String VERSION = "2.0.1";

    public static final String GITHUB = "http://gitlab.perfma-inc.net/PerfMa/XPocket";

    public static final String CLUB = "https://xpocket.perfma.com/";

}
