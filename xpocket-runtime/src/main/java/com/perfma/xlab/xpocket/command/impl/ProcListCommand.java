package com.perfma.xlab.xpocket.command.impl;

import com.perfma.xlab.xpocket.framework.spi.impl.CommandProcessor;
import com.perfma.xlab.xpocket.spi.command.CommandInfo;
import com.perfma.xlab.xpocket.spi.process.XPocketProcess;

import java.util.Set;

/**
 * @author xinxian
 * @create 2020-11-30 14:58
 **/
@CommandInfo(name = "proc_list", usage = "list process", index = 6)
public class ProcListCommand extends AbstractSystemCommand {

    @Override
    public void invoke(XPocketProcess process) throws Throwable {
        final Set<String> aliases = CommandProcessor.aliases;
        if (aliases != null && aliases.size() > 0) {
            for (String aliase : aliases) {
                process.output(aliase);
            }
        } else {
            process.output("process is empty, you can use the proc_save to add");
        }
        process.end();
    }
}
