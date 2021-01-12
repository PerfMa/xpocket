package com.perfma.xlab.xpocket.command.impl;

import com.perfma.xlab.xpocket.framework.spi.impl.XPocketStatusContext;
import com.perfma.xlab.xpocket.plugin.context.FrameworkPluginContext;
import com.perfma.xlab.xpocket.plugin.manager.PluginManager;
import com.perfma.xlab.xpocket.spi.command.CommandInfo;
import com.perfma.xlab.xpocket.utils.TerminalUtil;
import com.perfma.xlab.xpocket.utils.XPocketConstants;
import com.perfma.xlab.xpocket.spi.process.XPocketProcess;
import com.perfma.xlab.xpocket.spi.context.PluginBaseInfo;

/**
 * @author xinxian
 * @create 2020-09-08 14:38
 **/
@CommandInfo(name = "use", usage = "you can use command \"use [plugin_name]\" or \"use [plugin_name@namespace]\" to use the plugin",index=1)
public class UseCommand extends AbstractSystemCommand {

    @Override
    public void invoke(XPocketProcess process) {
        String[] args = process.getArgs();
        if (args == null || args[0] == null) {
            process.output("Please enter the plugin name");
        }
        String arg = args[0];
        String[] name_ns = arg.split("@");
        FrameworkPluginContext pluginContext = null;
        if (name_ns.length > 1) {
            pluginContext = PluginManager.getPlugin(name_ns[0], name_ns[1]);
        } else {
            pluginContext = PluginManager.getPlugin(name_ns[0], null);
        }
        if (pluginContext == null) {
            process.output("The plugin named [" + args[0] + "] does not exist" + TerminalUtil.lineSeparator);
        }

        if (pluginContext != null) {
            try {
                XPocketStatusContext.open(pluginContext,process);
                TerminalUtil.printHelp(process, pluginContext);
            } catch (Throwable throwable) {
                process.output("Display help information error : " + throwable.getMessage() + TerminalUtil.lineSeparator);
                XPocketStatusContext.open(PluginManager
                        .getPlugin(XPocketConstants.SYSTEM_PLUGIN_NAME,
                                XPocketConstants.SYSTEM_PLUGIN_NS),process);
            }
        }
        process.end();
    }
}
