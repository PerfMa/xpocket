package com.perfma.xlab.xpocket.framework.spi.impl.once;

import com.perfma.xlab.xpocket.plugin.base.NamedObject;

/**
 *
 * @author gongyu <yin.tong@perfma.com>
 */
public class OnceNamedObject implements NamedObject {

    private static final String NAME = "ONCE";

    @Override
    public String name() {
        return NAME;
    }
    
}
