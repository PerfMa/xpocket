package com.perfma.xlab.xpocket.framework.spi.impl.telnet;

import com.perfma.xlab.xpocket.plugin.base.NamedObject;

/**
 *
 * @author gongyu <yin.tong@perfma.com>
 */
public class TelnetNamedObject implements NamedObject {

    private static final String NAME = "TELNET";

    @Override
    public String name() {
        return NAME;
    }
}
