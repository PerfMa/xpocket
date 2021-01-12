package com.perfma.xlab.xpocket.spi.command.callback;

import com.perfma.xlab.xpocket.spi.process.XPocketProcess;

/**
 *
 * @author gongyu <yin.tong@perfma.com>
 */
public interface ProcessCallback {

    /**
     * 
     * @param process
     * @param cmd
     * @param args
     * @return
     * @throws Throwable 
     */
    String call(XPocketProcess process,String cmd,String[] args) throws Throwable;
    
}
