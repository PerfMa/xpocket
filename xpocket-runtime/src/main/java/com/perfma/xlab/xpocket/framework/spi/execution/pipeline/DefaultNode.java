package com.perfma.xlab.xpocket.framework.spi.execution.pipeline;

import com.perfma.xlab.xpocket.plugin.execution.Node;
import com.perfma.xlab.xpocket.spi.context.PluginBaseInfo;

/**
 * @author gongyu <yin.tong@perfma.com>
 */
public class DefaultNode implements Node {

    private final PluginBaseInfo context;
    private final String cmd;
    private final String[] args;

    public DefaultNode(PluginBaseInfo context, String cmd, String[] args) {
        this.context = context;

        String flag = context.getNamespace() + ".";
        int position = cmd.indexOf(flag);
        if (position >= 0) {
            this.cmd = cmd.substring(position + flag.length(), cmd.length());
        } else {
            this.cmd = cmd;
        }
        this.args = args;
    }

    @Override
    public PluginBaseInfo getPluginContext() {
        return context;
    }

    @Override
    public String getCmd() {
        return cmd;
    }

    @Override
    public String[] getArgs() {
        return args;
    }

}
