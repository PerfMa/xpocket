package com.perfma.xlab.xpocket.plugin.util;

import com.perfma.xlab.xpocket.plugin.base.NamedObject;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.ServiceLoader;

/**
 *
 * @author gongyu <yin.tong@perfma.com>
 */
public class ServiceLoaderUtils {

    public static final <V extends NamedObject> Map<String,V> loadServices(
            Class<V> clazz) {
        ServiceLoader<V> pluginLoaderLoader = ServiceLoader
                .load(clazz);
        Iterator<V> it = pluginLoaderLoader.iterator();
        Map<String,V> services = new HashMap<>();
        while(it.hasNext()) {
            V v = it.next();
            services.put(v.name(), v);
        }
        
        return services;
    }
    
}
