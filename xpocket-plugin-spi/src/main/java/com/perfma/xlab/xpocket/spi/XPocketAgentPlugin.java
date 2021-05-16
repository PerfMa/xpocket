package com.perfma.xlab.xpocket.spi;

import com.perfma.xlab.xpocket.spi.process.XPocketProcess;
import java.lang.instrument.Instrumentation;

/**
 * Abstract Definition for Plugin that attached on target JVM processor.
 * @author gongyu <yin.tong@perfma.com>
 */
public abstract class XPocketAgentPlugin implements XPocketPlugin {

    public void init(XPocketProcess process,Instrumentation inst) {
        doInit(inst);
        init(process);
    }

    protected abstract void doInit(Instrumentation inst);
    
}
