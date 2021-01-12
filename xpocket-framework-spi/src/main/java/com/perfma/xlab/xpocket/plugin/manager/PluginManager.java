package com.perfma.xlab.xpocket.plugin.manager;

import com.perfma.xlab.xpocket.plugin.context.FrameworkPluginContext;
import com.perfma.xlab.xpocket.plugin.loader.NonavailablePluginLoader;
import com.perfma.xlab.xpocket.plugin.loader.PluginLoader;

import java.util.Iterator;
import java.util.ServiceLoader;
import java.util.Set;

/**
 * @author gongyu <yin.tong@perfma.com>
 */
public class PluginManager {

    private static final PluginLoader PLUGIN_LOADER;

    static {
        ServiceLoader<PluginLoader> pluginLoaderLoader = ServiceLoader
                .load(PluginLoader.class);
        Iterator<PluginLoader> it = pluginLoaderLoader.iterator();

        if (it.hasNext()) {
            PLUGIN_LOADER = it.next();
        } else {
            PLUGIN_LOADER = new NonavailablePluginLoader();
        }
    }

    public static boolean loadPlugins(String resouceName) {
        return PLUGIN_LOADER.loadPlugins(resouceName);
    }

    public static Set<FrameworkPluginContext> getAvailablePlugins() {
        return PLUGIN_LOADER.getAvailablePlugins();
    }

    public static Set<FrameworkPluginContext> getAllPlugins() {
        return PLUGIN_LOADER.getAllPlugins();
    }

    public static FrameworkPluginContext getPlugin(String name, String namespace) {
        return PLUGIN_LOADER.getPlugin(name, namespace);
    }


    public static void addPlugin(FrameworkPluginContext context) {
        PLUGIN_LOADER.addPlugin(context);
    }

}
