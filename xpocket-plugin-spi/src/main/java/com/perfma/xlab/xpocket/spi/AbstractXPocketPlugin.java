package com.perfma.xlab.xpocket.spi;

import com.perfma.xlab.xpocket.spi.context.SessionContext;
import com.perfma.xlab.xpocket.spi.process.XPocketProcess;

/**
 * empty implementation of XPocketPlugin
 *
 * @author gongyu <yin.tong@perfma.com>
 */
public abstract class AbstractXPocketPlugin implements XPocketPlugin {

    @Override
    public void init(XPocketProcess process) {}

    @Override
    public void destory() throws Throwable {}

    @Override
    public void switchOn(SessionContext context) {}

    @Override
    public void switchOff(SessionContext context) {}

    @Override
    public void printLogo(XPocketProcess process) {}

}
