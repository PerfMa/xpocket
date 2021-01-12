package com.perfma.xlab.xpocket.command.impl;

import com.perfma.xlab.xpocket.spi.command.CommandInfo;
import com.perfma.xlab.xpocket.spi.process.XPocketProcess;

/**
 * @author xinxian
 * @create 2020-11-04 17:48
 **/
@CommandInfo(name = "trim", usage = "removed any leading and trailing whitespace", index = 11)
public class TrimCommand extends AbstractSystemCommand {

    @Override
    public void invoke(XPocketProcess process) throws Throwable {
        if (process.input() != null) {
            process.output(process.input().trim());
        } else {
            final String[] args = process.getArgs();
            if (args != null && args.length > 0) {
                StringBuilder sb = new StringBuilder();
                for (int i = 0; i < args.length; i++) {
                    sb.append(args[i]);
                }
                process.output(sb.toString().trim());
            }
        }
        process.end();
    }
}
