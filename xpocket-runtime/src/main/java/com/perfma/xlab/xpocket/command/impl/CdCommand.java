package com.perfma.xlab.xpocket.command.impl;

import com.perfma.xlab.xpocket.framework.spi.impl.XPocketStatusContext;
import com.perfma.xlab.xpocket.plugin.manager.PluginManager;
import com.perfma.xlab.xpocket.spi.command.CommandInfo;
import com.perfma.xlab.xpocket.spi.process.XPocketProcess;
import com.perfma.xlab.xpocket.utils.TerminalUtil;
import com.perfma.xlab.xpocket.utils.XPocketConstants;

/**
 * @author: TuGai
 **/
@CommandInfo(name = "cd", usage = "change current context to system", index = 13)
public class CdCommand extends AbstractSystemCommand {

    @Override
    public void invoke(XPocketProcess process) {
        try {
            XPocketStatusContext.open(PluginManager
                    .getPlugin(XPocketConstants.SYSTEM_PLUGIN_NAME,
                            XPocketConstants.SYSTEM_PLUGIN_NS), process);
            TerminalUtil.printHelp(process,
                    XPocketStatusContext.instance.currentPlugin());
            process.end();
        } catch (Throwable ex) {
            ex.printStackTrace();
        }
    }
}
