package com.perfma.xlab.xpocket.plugin.context;

import com.perfma.xlab.xpocket.spi.context.PluginBaseInfo;
import com.perfma.xlab.xpocket.spi.process.XPocketProcess;

/**
 * @author gongyu <yin.tong@perfma.com>
 */
public interface FrameworkPluginContext extends PluginBaseInfo {

    /**
     * Responsible for initialization
     *
     * @param process
     */
    void init(XPocketProcess process);

}
