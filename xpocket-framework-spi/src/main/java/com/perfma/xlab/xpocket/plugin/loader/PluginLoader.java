package com.perfma.xlab.xpocket.plugin.loader;


import com.perfma.xlab.xpocket.plugin.base.NamedObject;
import com.perfma.xlab.xpocket.plugin.context.FrameworkPluginContext;
import java.lang.instrument.Instrumentation;

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
    @Deprecated
    default boolean loadPlugins(String resouceName){
        throw new UnsupportedOperationException("It`s default implementation "
                + "for loadPlugins(String resouceName),what means your provider "
                + "never implemented this interface. ");
    }
    
        /**
     * Load plugins and build it into FrameworkPluginContexts
     *
     * @param resouceName 如：META-INF/xpocket.def
     * @param isOnLoad is onLoad or onAttach when agent mode
     * @param inst Instrumentation instance of Java Agent
     * @return
     */
    default boolean loadPlugins(String resouceName,boolean isOnLoad,Instrumentation inst){
        throw new UnsupportedOperationException("It`s default implementation "
                + "for loadPlugins(String resouceName,boolean isOnLoad),what "
                + "means your provider never implemented this interface. ");
    }

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
