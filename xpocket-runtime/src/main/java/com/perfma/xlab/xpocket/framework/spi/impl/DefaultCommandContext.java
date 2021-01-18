package com.perfma.xlab.xpocket.framework.spi.impl;

import java.util.Objects;

import com.perfma.xlab.xpocket.spi.command.XPocketCommand;
import com.perfma.xlab.xpocket.spi.context.JavaTarget;
import com.perfma.xlab.xpocket.spi.context.PluginType;
import com.perfma.xlab.xpocket.spi.context.CommandBaseInfo;

/**
 * @author gongyu <yin.tong@perfma.com>
 */
public class DefaultCommandContext implements CommandBaseInfo {

    private final String name;

    private final String usage;

    private final int index;

    private PluginType type;
    private JavaTarget target;

    private final XPocketCommand instance;

    public DefaultCommandContext(String name, String usage, int index, XPocketCommand instance) {
        this.name = name;
        this.usage = usage;
        this.index = index;
        this.instance = instance;
    }

    @Override
    public String name() {
        return name;
    }

    @Override
    public String usage() {
        return usage;
    }

    @Override
    public int index() {
        return index;
    }

    @Override
    public XPocketCommand instance() {
        return instance;
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final DefaultCommandContext other = (DefaultCommandContext) obj;
        return Objects.equals(this.name, other.name);
    }

}
