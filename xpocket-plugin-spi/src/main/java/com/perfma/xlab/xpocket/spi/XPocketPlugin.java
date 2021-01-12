package com.perfma.xlab.xpocket.spi;

import com.perfma.xlab.xpocket.spi.context.SessionContext;
import com.perfma.xlab.xpocket.spi.process.XPocketProcess;

/**
 * @author TONG YIN <yin.tong@perfma.com>
 */
public interface XPocketPlugin {

    /**
     * init the plugin
     *
     * @param process process info of current xpocket runtime
     */
    void init(XPocketProcess process);

    /**
     * destory the resource current plugin used.
     * @throws java.lang.Throwable
     */
    void destory() throws Throwable;
    
    /**
     * when XPocket switched on this plugin,it will call this method
     * @param context 
     */
    void switchOn(SessionContext context);
    
    /**
     * when XPocket switched off or leave this plugin,it will call this method
     * @param context 
     */
    void switchOff(SessionContext context);
    
    /**
     * print plugin`s own logo when switch in this plugin
     * @param process 
     */
    void printLogo(XPocketProcess process);

}
