package com.perfma.xlab.xpocket.plugin.manager;

import com.perfma.xlab.xpocket.plugin.ui.UIEngine;
import com.perfma.xlab.xpocket.plugin.util.Constants;
import com.perfma.xlab.xpocket.plugin.util.ServiceLoaderUtils;
import java.lang.instrument.Instrumentation;

import java.util.Map;

/**
 * @author gongyu <yin.tong@perfma.com>
 */
public class MainUIManager {

    private static final UIEngine coreUI;

    static {
        Map<String,UIEngine> UIs = ServiceLoaderUtils.loadServices(UIEngine.class);
        String run_mode = System.getProperty(Constants.RUN_MODE_KEY, Constants.DEFAULT_RUN_MODE).toUpperCase();
        if(UIs.containsKey(run_mode)) {
            coreUI = UIs.get(run_mode);
        } else {
            coreUI = UIs.get(Constants.DEFAULT_RUN_MODE);
        }
        
        if(coreUI == null) {
            throw new RuntimeException("There is no UI implementation exists in environment!");
        }
    }

    @Deprecated
    public static void start(String def, String[] args) {
        coreUI.start(def, args);
    }
    
    public static void start(String[] def, String[] args,Instrumentation inst) {
        coreUI.start(def, args, inst);
    }

}
