package com.perfma.xlab.xpocket.framework.spi.impl;

import com.perfma.xlab.xpocket.plugin.base.NamedObject;

/**
 *
 * @author gongyu <yin.tong@perfma.com>
 */
public class DefaultNamedObject implements NamedObject {
     
    private static final String NAME = "DEFAULT";
    
    @Override
    public String name() {
        return NAME;
    }

}
