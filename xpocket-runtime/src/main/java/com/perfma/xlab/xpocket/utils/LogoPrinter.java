package com.perfma.xlab.xpocket.utils;

import com.perfma.xlab.xpocket.spi.process.XPocketProcess;


/**
 * @author xinxian
 * @create 2020-09-25 14:21
 **/
public class LogoPrinter {
    public static void print(XPocketProcess process) {
        process.output(XPocketBanner.welcome());
    }

}
