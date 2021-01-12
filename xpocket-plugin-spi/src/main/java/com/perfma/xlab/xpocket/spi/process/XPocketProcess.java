package com.perfma.xlab.xpocket.spi.process;

/**
 * @author gongyu <yin.tong@perfma.com>
 */
public interface XPocketProcess {

    /**
     * get command name
     *
     * @return
     */
    String getCmd();

    /**
     * get command args
     *
     * @return
     */
    String[] getArgs();

    /**
     * get input for piped execution
     *
     * @return
     */
    String input();

    /**
     * output
     *
     * @param output
     */
    void output(String output);

    /**
     * process end
     */
    void end();

    /**
     * Register to monitor
     *
     * @param action
     */
    void register(XPocketProcessAction action);

    /**
     * unregister
     *
     * @param action
     */
    void unregister(XPocketProcessAction action);

}
