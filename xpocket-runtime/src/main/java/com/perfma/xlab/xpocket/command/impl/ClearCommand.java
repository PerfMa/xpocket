package com.perfma.xlab.xpocket.command.impl;

import com.perfma.xlab.xpocket.command.AbstractSystemCommand;
import com.perfma.xlab.xpocket.spi.command.CommandInfo;
import com.perfma.xlab.xpocket.spi.process.XPocketProcess;

/**
 * @author gongyu <yin.tong@perfma.com>
 */
@CommandInfo(name = "clear", usage = "clear the screan", index = 16)
public class ClearCommand extends AbstractSystemCommand {

    @Override
    public void invoke(XPocketProcess process) throws Throwable {
        getReader().clearScreen();
        process.end();
    }

}
