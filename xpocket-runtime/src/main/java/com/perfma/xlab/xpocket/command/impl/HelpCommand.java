package com.perfma.xlab.xpocket.command.impl;

import com.perfma.xlab.xpocket.framework.spi.impl.XPocketStatusContext;
import com.perfma.xlab.xpocket.plugin.context.FrameworkPluginContext;
import com.perfma.xlab.xpocket.plugin.manager.CommandManager;
import com.perfma.xlab.xpocket.plugin.manager.PluginManager;
import com.perfma.xlab.xpocket.spi.command.CommandInfo;
import com.perfma.xlab.xpocket.spi.process.XPocketProcess;
import com.perfma.xlab.xpocket.utils.TerminalUtil;
import com.perfma.xlab.xpocket.spi.context.CommandBaseInfo;

/**
 * @author xinxian
 * @create 2020-09-07 16:23
 */
@CommandInfo(name = "help", usage = "command help info and you can use \"help <cmd>\" to see the detailed usage of the command")
public class HelpCommand extends AbstractSystemCommand {

    public HelpCommand() {
    }

    @Override
    public void invoke(XPocketProcess process) {
        final String[] args = process.getArgs();
        FrameworkPluginContext context = (FrameworkPluginContext)XPocketStatusContext.instance.currentPlugin();
        if (args != null && args.length >= 1 && !"".equals(args[0])) {
            final String arg = args[0];
            String pluginName = context.getName();
            String pluginNamespace = context.getNamespace();
            String cmd = null;

            StringBuilder[] params = new StringBuilder[3];

            int flag = 0;
            params[flag] = new StringBuilder();
            for (char c : arg.toCharArray()) {
                switch (c) {
                    case '.':
                        if (flag == 0) {
                            flag = 1;
                            params[1] = new StringBuilder();
                            break;
                        }
                    case '@':
                        if (flag < 2) {
                            flag = 2;
                            params[flag] = new StringBuilder();
                            break;
                        }
                    default:
                        params[flag].append(c);
                }
            }

            if (params[1] == null && params[2] == null) {
                cmd = params[0].toString();
            } else {
                pluginName = params[0].length() > 0
                        ? params[0].toString() : pluginName;
                pluginNamespace = params[2] != null
                        ? params[2].toString() : pluginNamespace;
                cmd = params[1] != null ? params[1].toString() : cmd;
            }

            context = PluginManager.getPlugin(pluginName, pluginNamespace);

            if (context == null) {
                context = CommandManager.findByCommand(arg);
            }

            if (context == null) {
                process.output(String.format("NO SUCH ITEMS %s", arg));
            } else {
                context.init(process);
                if (cmd == null) {
                    TerminalUtil.printHelp(process, context);
                    process.output(TerminalUtil.lineSeparator);
                } else {
                    CommandBaseInfo commandCtx = context.getCommandContext(cmd);
                    if (commandCtx != null) {
                        process.output(commandCtx.usage()
                                + TerminalUtil.lineSeparator);
                        if (commandCtx.instance().details(cmd) != null
                                && !commandCtx.instance().details(cmd).isEmpty()) {
                            process.output(TerminalUtil.lineSeparator);
                            process.output(commandCtx.instance().details(cmd)
                                    + TerminalUtil.lineSeparator);
                        }
                    } else {
                        process.output(String.format("NO SUCH COMMAND %s in %s@%s", cmd,
                                pluginName, pluginNamespace.toUpperCase()));
                    }
                }
            }

        } else {
            TerminalUtil.printHelp(process, context);
        }
        process.end();
    }
}
