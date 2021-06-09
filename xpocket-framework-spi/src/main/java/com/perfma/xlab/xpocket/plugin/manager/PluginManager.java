package com.perfma.xlab.xpocket.plugin.manager;

import com.perfma.xlab.xpocket.plugin.context.FrameworkPluginContext;
import com.perfma.xlab.xpocket.plugin.loader.NonavailablePluginLoader;
import com.perfma.xlab.xpocket.plugin.loader.PluginLoader;
import com.perfma.xlab.xpocket.plugin.util.Constants;
import com.perfma.xlab.xpocket.plugin.util.ServiceLoaderUtils;

import java.util.Map;
import java.util.Set;

/**
 * @author gongyu <yin.tong@perfma.com>
 */
public class PluginManager {

    private static final PluginLoader PLUGIN_LOADER;

    static {
        Map<String,PluginLoader> loaders = ServiceLoaderUtils
                .loadServices(PluginLoader.class);
        String run_mode = System.getProperty(Constants.RUN_MODE_KEY, 
                Constants.DEFAULT_RUN_MODE).toUpperCase();
        
        if (loaders.containsKey(run_mode)) {
            PLUGIN_LOADER = loaders.get(run_mode);
        } else {
            PLUGIN_LOADER = loaders.getOrDefault(Constants.DEFAULT_RUN_MODE,
                    new NonavailablePluginLoader());
        }

    }

    public static boolean loadPlugins(String resouceName) {
        return PLUGIN_LOADER.loadPlugins(resouceName);
    }
    
    public static boolean loadPlugins(String resouceName,boolean isOnLoad) {
        return PLUGIN_LOADER.loadPlugins(resouceName,isOnLoad);
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
