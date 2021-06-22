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
@CommandInfo(name = "detach", usage = "disconnect with the java process,detach", index = 99)
public class DetachCommand extends AbstractSystemCommand {
    
    @Override
    public void invoke(XPocketProcess process, SessionContext context) throws Throwable {
        XPocketProcessTemplate.execute(process, 
                (String cmd, String[] args) -> 
                {
                    XPocketConstants.DEFAULT_ADAPTOR.detach(process);
                    return "Detach with target process success";
                });
    }
   
}
