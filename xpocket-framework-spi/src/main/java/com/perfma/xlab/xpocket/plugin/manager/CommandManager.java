package com.perfma.xlab.xpocket.plugin.manager;

import com.perfma.xlab.xpocket.plugin.command.CommandLoader;
import com.perfma.xlab.xpocket.plugin.context.FrameworkPluginContext;

import java.util.Iterator;
import java.util.Map;
import java.util.ServiceLoader;
import java.util.Set;

/**
 * @author gongyu <yin.tong@perfma.com>
 */
public class CommandManager {

    private static CommandLoader cmdLoader;

    static {
        ServiceLoader<CommandLoader> pluginLoaderLoader = ServiceLoader
                .load(CommandLoader.class);
        Iterator<CommandLoader> it = pluginLoaderLoader.iterator();

        if (it.hasNext()) {
            cmdLoader = it.next();
        } else {
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
