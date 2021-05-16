package com.perfma.xlab.xpocket.framework.spi.impl.agent;

import com.perfma.xlab.xpocket.plugin.base.NamedObject;

/**
 *
 * @author gongyu <yin.tong@perfma.com>
 */
public class AgentNamedObject implements NamedObject  {

    private static final String NAME = "AGENT";

    @Override
    public String name() {
        return NAME;
    }
    
}
