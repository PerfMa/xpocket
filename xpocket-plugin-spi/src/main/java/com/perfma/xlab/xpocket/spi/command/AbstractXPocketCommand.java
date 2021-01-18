package com.perfma.xlab.xpocket.spi.command;

import com.perfma.xlab.xpocket.spi.context.SessionContext;
import com.perfma.xlab.xpocket.spi.XPocketPlugin;
import com.perfma.xlab.xpocket.spi.process.XPocketProcess;

/**
 * @author gongyu <yin.tong@perfma.com>
 */
public abstract class AbstractXPocketCommand implements XPocketCommand {

    protected XPocketPlugin plugin;

    @Override
    public boolean isPiped() {
        //false for default
        return false;
    }

    @Override
    public void init(XPocketPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean isAvailableNow(String cmd) {
        return true;
    }

    @Override
    public String details(String cmd) {
        return null;
    }

    @Override
    public void invoke(XPocketProcess process, SessionContext context) throws Throwable {
        invoke(process);
    }

    public void invoke(XPocketProcess process) throws Throwable {
    }

    @Override
    public String[] tips() {
        return null;
    }

}
