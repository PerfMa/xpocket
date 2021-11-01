package com.perfma.xlab.xpocket.command.impl;

import com.perfma.xlab.xpocket.command.AbstractSystemCommand;
import com.perfma.xlab.xpocket.framework.spi.impl.CommandProcessor;
import com.perfma.xlab.xpocket.spi.command.CommandInfo;
import com.perfma.xlab.xpocket.spi.process.XPocketProcess;

/**
 * @author xinxian
 * @create 2020-12-09 16:21
 **/
@CommandInfo(name = "proc_delete", usage = "proc_delete [alias]", index = 5)
public class ProcDeleteCommand extends AbstractSystemCommand {

    @Override
    public void invoke(XPocketProcess process) throws Throwable {
        final String[] args = process.getArgs();
        if (args == null || args.length != 1) {
            process.output("command format error: \"proc_delete [alias]\"");
            process.end();
            return;
        }
        if (CommandProcessor.solutionMap.containsKey(args[0])) {
            CommandProcessor.solutionMap.remove(args[0]);
            CommandProcessor.aliases.remove(args[0]);
        }
        process.output("operation success...");
        process.end();
    }
}
