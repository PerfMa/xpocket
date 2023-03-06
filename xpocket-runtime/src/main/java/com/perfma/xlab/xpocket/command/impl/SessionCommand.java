package com.perfma.xlab.xpocket.command.impl;

import com.perfma.xlab.xpocket.command.AbstractSystemCommand;
import com.perfma.xlab.xpocket.framework.spi.impl.XPocketStatusContext;
import com.perfma.xlab.xpocket.spi.command.CommandInfo;
import com.perfma.xlab.xpocket.spi.process.XPocketProcess;
import com.perfma.xlab.xpocket.utils.TerminalUtil;

/**
 * @author xinxian
 * @create 2020-10-21 17:56
 **/
@CommandInfo(name = "session", usage = "Show XPocket Status Context", index = 12)
public class SessionCommand extends AbstractSystemCommand {

    @Override
    public void invoke(XPocketProcess process) {
        final XPocketStatusContext instance = XPocketStatusContext.instance;
        final int pid = instance.pid();
        if (pid >= 0) {
            process.output("--------------------------" + TerminalUtil.lineSeparator);
            process.output("Has been attached : true" + TerminalUtil.lineSeparator);
            process.output("Pid               : " + pid + TerminalUtil.lineSeparator);
            process.output("--------------------------" + TerminalUtil.lineSeparator);
        }
        process.end();
    }
}
