package com.perfma.xlab.xpocket.spi;

import com.perfma.xlab.xpocket.spi.process.XPocketProcess;
import java.lang.instrument.Instrumentation;

/**
 *
 * @author gongyu <yin.tong@perfma.com>
 */
public abstract class AbstractXPocketAgentPlugin extends AbstractXPocketPlugin 
        implements XPocketAgentPlugin {

    @Override
    public void init(XPocketProcess process, Instrumentation inst, 
            boolean isOnLoad) {}

}
