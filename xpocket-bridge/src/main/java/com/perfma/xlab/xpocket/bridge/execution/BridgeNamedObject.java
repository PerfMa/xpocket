package com.perfma.xlab.xpocket.bridge.execution;

import com.perfma.xlab.xpocket.plugin.base.NamedObject;

/**
 * @author xinxian
 * @create 2021-03-23 16:43
 **/
public class BridgeNamedObject implements NamedObject {
    private static final String NAME = "BRIDGE";
    @Override
    public String name() {
        return NAME;
    }
}
