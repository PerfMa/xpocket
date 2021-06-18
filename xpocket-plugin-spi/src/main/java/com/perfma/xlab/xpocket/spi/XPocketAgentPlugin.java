package com.perfma.xlab.xpocket.spi;

import com.perfma.xlab.xpocket.spi.process.XPocketProcess;
import java.lang.instrument.Instrumentation;

/**
 * Abstract Definition for Plugin that attached on target JVM processor.
 * @author gongyu <yin.tong@perfma.com>
 */
public interface XPocketAgentPlugin extends XPocketPlugin {

    void init(XPocketProcess process,Instrumentation inst,boolean isOnLoad);
    
}
