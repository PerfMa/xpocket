package com.perfma.xlab.xpocket.plugin.manager;

import com.perfma.xlab.xpocket.ui.UIEngine;

import java.util.Iterator;
import java.util.ServiceLoader;

/**
 * @author gongyu <yin.tong@perfma.com>
 */
public class MainUIManager {

    private static final UIEngine coreUI;

    static {
        ServiceLoader<UIEngine> pluginLoaderLoader = ServiceLoader
                .load(UIEngine.class);
        Iterator<UIEngine> it = pluginLoaderLoader.iterator();

        if (it.hasNext()) {
            coreUI = it.next();
        } else {
            throw new RuntimeException("There is no UI implementation exists in environment!");
        }
    }

    public static void start(String def, String[] args) {
        coreUI.start(def, args);
    }

}
