package com.perfma.xlab.xpocket.framework.spi.impl.agent;

import com.perfma.xlab.xpocket.plugin.manager.MainUIManager;
import com.perfma.xlab.xpocket.plugin.util.Constants;
import java.lang.instrument.Instrumentation;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Properties;

/**
 *
 * @author gongyu <yin.tong@perfma.com>
 */
public class XPocketAgentLauncher {

    private static final String[] DEFS = {"xpocket.def"};
    
    public static void start(Properties args,Instrumentation inst,boolean isOnLoad) {
        
        Thread.currentThread()
                .setContextClassLoader(XPocketAgentLauncher.class.getClassLoader());
        
        List<String> argsForCore = new ArrayList<>();
        argsForCore.add("-onload");
        argsForCore.add(Boolean.toString(isOnLoad));
        
        if(System.getProperty(Constants.RUN_MODE_KEY) == null) {
            System.setProperty(Constants.RUN_MODE_KEY,
                    AgentNamedObject.NAME);
        }
        
        if(args.containsKey("XPOCKET_HOME")) {
            System.setProperty("XPOCKET_HOME", 
                args.getProperty("XPOCKET_HOME"));
        }
        
        if(args.containsKey("XPOCKET_PLUGIN_PATH")) {
            System.setProperty("XPOCKET_PLUGIN_PATH", 
                args.getProperty("XPOCKET_PLUGIN_PATH"));
        }
        
        if(args.containsKey("XPOCKET_CONFIG_PATH")) {
            System.setProperty("XPOCKET_CONFIG_PATH", 
                args.getProperty("XPOCKET_CONFIG_PATH"));
        }
        
        if(args.containsKey("XPOCKET_ONLY_AGENT")) {
            System.setProperty("XPOCKET_ONLY_AGENT", 
                args.getProperty("XPOCKET_ONLY_AGENT"));
        }
        
        Enumeration e = args.propertyNames();
        
        while(e.hasMoreElements()) {
            try {
                String name = (String)e.nextElement();
                argsForCore.add("-" + name);
                argsForCore.add(args.getProperty(name));
            } catch (Throwable ex) {
                //ignore it
            }
        }

        String[] argArrayForCore =  new String[argsForCore.size()];
        argsForCore.toArray(argArrayForCore);
        MainUIManager.start(DEFS, argArrayForCore, inst);
    }
    
}
