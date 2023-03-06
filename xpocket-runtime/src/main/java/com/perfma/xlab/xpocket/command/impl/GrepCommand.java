package com.perfma.xlab.xpocket.command.impl;

import com.perfma.xlab.xpocket.command.AbstractSystemCommand;
import com.perfma.xlab.xpocket.spi.command.CommandInfo;
import com.perfma.xlab.xpocket.spi.process.XPocketProcess;

/**
 * @author gongyu <yin.tong@perfma.com>
 */
@CommandInfo(name = "grep", usage = "grep -I [subText] handle content from pipe", index = 9)
public class GrepCommand extends AbstractSystemCommand {

    @Override
    public boolean isPiped() {
        return true;
    }

    @Override
    public void invoke(XPocketProcess process) throws Throwable {

        String content = process.input();
        String[] args = process.getArgs() == null
                ? new String[0]
                : process.getArgs();
        String subText = null;
        boolean ignoreCase = true;

        for (int i = 0; i < args.length; i++) {
            String arg = args[i];
            switch (arg) {
                case "-I":
                    ignoreCase = false;
                    break;
                default:
                    subText = arg;
            }
        }

        if (content == null || subText == null) {
            process.end();
        }

        if (ignoreCase && content.toLowerCase().contains(subText.toLowerCase())) {
            process.output(content);
        } else if (content.contains(subText)) {
            process.output(content);
        }

        process.end();
    }
}
