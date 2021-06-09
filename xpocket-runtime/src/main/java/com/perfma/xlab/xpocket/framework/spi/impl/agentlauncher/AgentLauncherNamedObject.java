package com.perfma.xlab.xpocket.framework.spi.impl.agentlauncher;

import com.perfma.xlab.xpocket.plugin.base.NamedObject;

/**
 *
 * @author gongyu <yin.tong@perfma.com>
 */
public class AgentLauncherNamedObject implements NamedObject  {

    private static final String NAME = "AGENT-LAUNCHER";

    @Override
    public String name() {
        return NAME;
    }
    
}
