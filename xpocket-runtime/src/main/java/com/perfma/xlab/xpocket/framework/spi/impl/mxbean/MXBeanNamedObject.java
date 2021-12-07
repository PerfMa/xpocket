package com.perfma.xlab.xpocket.framework.spi.impl.mxbean;

import com.perfma.xlab.xpocket.framework.spi.impl.telnet.*;
import com.perfma.xlab.xpocket.plugin.base.NamedObject;

/**
 *
 * @author gongyu <yin.tong@perfma.com>
 */
public class MXBeanNamedObject implements NamedObject {

    private static final String NAME = "MXBEAN";

    @Override
    public String name() {
        return NAME;
    }
}
