package com.perfma.xlab.xpocket.command.impl;

import com.perfma.xlab.xpocket.command.AbstractSystemCommand;
import com.perfma.xlab.xpocket.plugin.context.FrameworkPluginContext;
import com.perfma.xlab.xpocket.plugin.manager.CommandManager;
import com.perfma.xlab.xpocket.plugin.manager.PluginManager;
import com.perfma.xlab.xpocket.spi.command.CommandInfo;
import com.perfma.xlab.xpocket.spi.process.XPocketProcess;
import com.perfma.xlab.xpocket.utils.TerminalUtil;

import java.util.Arrays;
import java.util.Set;

/**
 * @author xinxian
 * @create 2020-09-08 14:37
 **/
@CommandInfo(name = "plugins", usage = "you can use the \"plugins\" command to list available plugins",index=0)
public class ListPluginsCommand extends AbstractSystemCommand {

    @Override
    public void invoke(XPocketProcess process) {
        final String[] args = process.getArgs();
        if (args != null && args.length > 0 && "-a".equalsIgnoreCase(args[0])) {
            CommandManager.getCmdTable().entrySet().forEach(e -> {
                String plugin = e.getKey();
                String cmd = Arrays.toString(e.getValue().toArray());
                process.output("@|white " + plugin + ": |@" + TerminalUtil.lineSeparator);
                process.output("   ");
                process.output("@|green " + cmd + " |@" + TerminalUtil.lineSeparator);
            });
        } else {
            
            final Set<FrameworkPluginContext> availablePlugins = PluginManager.getAvailablePlugins();
            process.output("Valid Plugins" + TerminalUtil.lineSeparator);
            process.output("@|white " + fillSpace(" plugin_name", 15) + " |@     @|white  " + "namespace" + " |@" + TerminalUtil.lineSeparator);
            if (availablePlugins != null) {
                availablePlugins.forEach(item -> {
                    process.output("@|yellow  " + fillSpace(item.getName(), 15) + " |@  :  @|magenta " + item.getNamespace().toUpperCase() + " |@" + TerminalUtil.lineSeparator);
                });
            }
        }
        process.end();
    }

    @Override
    public String[] tips() {
        return new String[]{"you can use command \"use <plugin_name>\" or \"use <plugin_name@namespace>\" to use the plugin"}; 
    }
    
    

    private String fillSpace(String src, int size) {

        StringBuilder dist = new StringBuilder(size);

        if (src.length() < size) {
            dist.append(src);

            for (int i = 0; i < size - src.length(); i++) {
                dist.append(' ');
            }

        } else {
            return src;
        }

        return dist.toString();
    }
}
