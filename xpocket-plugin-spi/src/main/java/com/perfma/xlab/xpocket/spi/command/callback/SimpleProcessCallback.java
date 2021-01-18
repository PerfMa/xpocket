package com.perfma.xlab.xpocket.spi.command.callback;

/**
 * @author gongyu <yin.tong@perfma.com>
 */
public interface SimpleProcessCallback {

    /**
     * @param cmd
     * @param args
     * @return
     * @throws java.lang.Throwable
     */
    String call(String cmd, String[] args) throws Throwable;

}
