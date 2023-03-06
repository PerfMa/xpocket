package com.perfma.xlab.xpocket.command.impl;

import com.perfma.xlab.xpocket.command.AbstractSystemCommand;
import com.perfma.xlab.xpocket.console.EndOfInputException;
import com.perfma.xlab.xpocket.spi.command.CommandInfo;
import com.perfma.xlab.xpocket.spi.process.XPocketProcess;

/**
 * @author xinxian
 * @create 2020-09-09 14:46
 **/
@CommandInfo(name = "quit", usage = "quit XPocket", index = 51)
public class QuitCommand extends AbstractSystemCommand {
    @Override
    public void invoke(XPocketProcess process) {
        throw new EndOfInputException();
    }
}
