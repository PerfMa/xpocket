package com.perfma.xlab.xpocket.utils;

import java.lang.management.ManagementFactory;

/**
 *
 * @author gongyu <yin.tong@perfma.com>
 */
public class ProcessUtil {

    private static final String CURRENT_PID;
    
    static {
        String name = ManagementFactory.getRuntimeMXBean().getName();
        CURRENT_PID = name.split("@")[0].trim();
    }
    
    public static String getCurrentPid() {
        return CURRENT_PID;
    }
    
}
