package com.perfma.xlab.xpocket.spi.process;

/**
 * @author gongyu <yin.tong@perfma.com>
 */
public interface XPocketProcessAction {

    void userInput(String input) throws Throwable;

    void interrupt() throws Throwable;

}
