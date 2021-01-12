package com.perfma.xlab.xpocket.plugin.loader;


import com.perfma.xlab.xpocket.plugin.context.FrameworkPluginContext;

import java.util.Set;
import java.util.TreeSet;
import com.perfma.xlab.xpocket.spi.context.PluginBaseInfo;

/**
 * @author gongyu <yin.tong@perfma.com>
 */
public class NonavailablePluginLoader implements PluginLoader {

    private static final Set<FrameworkPluginContext> emptySet = new TreeSet<>();

    @Override
    public boolean loadPlugins(String resouceName) {
        return true;
    }

    @Override
    public Set<FrameworkPluginContext> getAvailablePlugins() {
        return emptySet;
    }

    @Override
    public Set<FrameworkPluginContext> getAllPlugins() {
        return emptySet;
    }

    @Override
    public void addPlugin(FrameworkPluginContext pluginContext) {
        // nop
    }

    @Override
    public FrameworkPluginContext getPlugin(String name,String namespace) {
        return null;
    }
}
