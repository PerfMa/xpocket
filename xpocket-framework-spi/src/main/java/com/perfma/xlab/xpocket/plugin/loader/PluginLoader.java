package com.perfma.xlab.xpocket.plugin.loader;


import com.perfma.xlab.xpocket.plugin.base.NamedObject;
import com.perfma.xlab.xpocket.plugin.context.FrameworkPluginContext;

import java.util.Set;

/**
 * @author gongyu <yin.tong@perfma.com>
 */
public interface PluginLoader extends NamedObject {

    /**
     * Load plugins and build it into FrameworkPluginContexts
     *
     * @param resouceName 如：META-INF/xpocket.def
     * @return
     */
    boolean loadPlugins(String resouceName);

    /**
     * Gets a list of available plug-ins
     *
     * @return
     */
    Set<FrameworkPluginContext> getAvailablePlugins();

    /**
     * Get all plugins
     *
     * @return
     */
    Set<FrameworkPluginContext> getAllPlugins();

    /**
     * Get the plug-in by its name and namespace
     *
     * @param name
     * @param namespace
     * @return
     */
    FrameworkPluginContext getPlugin(String name, String namespace);

    /**
     * Add the plugin
     *
     * @param pluginContext
     */
    void addPlugin(FrameworkPluginContext pluginContext);
}
