package com.perfma.xlab.xpocket.command.impl;

import com.perfma.xlab.xpocket.command.AbstractSystemCommand;
import com.perfma.xlab.xpocket.spi.command.CommandInfo;
import com.perfma.xlab.xpocket.spi.process.XPocketProcess;
import com.perfma.xlab.xpocket.utils.XPocketConstants;

/**
 * @author xinxian
 * @create 2020-12-09 16:01
 **/
@CommandInfo(name = "version", usage = "XPocket version info", index = 15)
public class VersionCommand extends AbstractSystemCommand {

    @Override
    public void invoke(XPocketProcess process) throws Throwable {
        process.output("@|green Initiator : PerfMa |@");
        process.output("@|green version   : " + XPocketConstants.VERSION + " |@");
        process.output("@|green github    : " + XPocketConstants.GITHUB + " |@");
        process.end();
    }
}
