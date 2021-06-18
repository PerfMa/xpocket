package com.perfma.xlab.xpocket.command.impl;

import com.perfma.xlab.xpocket.spi.command.CommandInfo;
import com.perfma.xlab.xpocket.spi.command.XPocketProcessTemplate;
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
        XPocketProcessTemplate.execute(process, 
                (String cmd, String[] args) -> 
                {
                    String errorInfo = XPocketConstants.DEFAULT_ADAPTOR.attach(process, args[0]);
                    if("OK".equalsIgnoreCase(errorInfo)) {
                        return String.format("Attach to target process success : %s",
                                args[0]);
                    } else {
                        return String.format("ERROR : %s", errorInfo);
                    }
                });
    }
   
}
