package com.perfma.xlab.xpocket.command.impl;

import com.perfma.xlab.xpocket.command.AbstractSystemCommand;
import com.perfma.xlab.xpocket.spi.command.CommandInfo;
import com.perfma.xlab.xpocket.spi.process.XPocketProcess;

/**
 * @author: yin.tong
 **/
@CommandInfo(name = "echo", usage = "print input strings", index = 99)
public class EchoCommand extends AbstractSystemCommand {

    @Override
    public void invoke(XPocketProcess process) {
        try {
            if(process.getArgs() != null && process.getArgs().length > 0) {
                process.output(process.getArgs()[0]);
            }
            process.end();
        } catch (Throwable ex) {
            ex.printStackTrace();
        }
    }
}
