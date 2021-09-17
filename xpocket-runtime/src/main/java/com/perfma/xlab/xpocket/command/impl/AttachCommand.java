package com.perfma.xlab.xpocket.command.impl;

import com.perfma.xlab.xpocket.spi.command.CommandInfo;
import com.perfma.xlab.xpocket.spi.context.SessionContext;
import com.perfma.xlab.xpocket.spi.process.XPocketProcess;
import com.perfma.xlab.xpocket.utils.XPocketConstants;

/**
 *
 * @author gongyu <yin.tong@perfma.com>
 */
@CommandInfo(name = "attach", usage = "attach on a java process,attach [pid]", index = 99)
public class AttachCommand extends AbstractSystemCommand {

    @Override
    public void invoke(XPocketProcess process, SessionContext context) throws Throwable {
        String result;
        try {
            String pid = process.getArgs()[0];
            String errorInfo = XPocketConstants.DEFAULT_ADAPTOR.attach(process, pid);
            if ("OK".equalsIgnoreCase(errorInfo)) {
                result = String.format("Attach to target process success : %s",
                        pid);
                process.output(result);
            } else {
                result = String.format("ERROR : %s", errorInfo);
                process.output(result);
                process.end();
            }
        } catch (Throwable ex) {
            result = String.format("ERROR : %s", ex.getMessage());
            process.output(result);
            process.end();
        }

    }
}
