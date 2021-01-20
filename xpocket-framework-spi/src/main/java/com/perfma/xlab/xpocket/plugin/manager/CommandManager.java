package com.perfma.xlab.xpocket.plugin.manager;

import com.perfma.xlab.xpocket.plugin.command.CommandLoader;
import com.perfma.xlab.xpocket.plugin.context.FrameworkPluginContext;
import com.perfma.xlab.xpocket.plugin.util.Constants;
import com.perfma.xlab.xpocket.plugin.util.ServiceLoaderUtils;

import java.util.Map;
import java.util.Set;

/**
 * @author gongyu <yin.tong@perfma.com>
 */
public class CommandManager {

    private static final CommandLoader cmdLoader;

    static {
        
        Map<String,CommandLoader> loaders = ServiceLoaderUtils.loadServices(CommandLoader.class);
        String run_mode = System.getProperty(Constants.RUN_MODE_KEY, Constants.DEFAULT_RUN_MODE).toUpperCase();

        if (loaders.containsKey(run_mode)) {
            cmdLoader = loaders.get(run_mode);
        } else {
            cmdLoader = loaders.get(Constants.DEFAULT_RUN_MODE);
        }
        
        if(cmdLoader == null) {
            throw new RuntimeException("There is no COMMAND implementation exists in environment!");
        }
    }

    public static Map<String, Set<String>> getCmdTable() {
        return cmdLoader.getCmdTable();
    }

    public static FrameworkPluginContext findByCommand(String command) {
        return cmdLoader.findByCommand(command);
    }

    public static boolean addCommand(Set<FrameworkPluginContext> contexts) {
        return cmdLoader.addCommand(contexts);
    }

    public static Set<String> cmdSet() {
        return cmdLoader.cmdSet();
    }

}
