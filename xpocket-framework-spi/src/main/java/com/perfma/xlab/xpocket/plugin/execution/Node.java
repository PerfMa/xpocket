package com.perfma.xlab.xpocket.plugin.execution;


import com.perfma.xlab.xpocket.spi.context.PluginBaseInfo;

/**
 * Encapsulate command information
 *
 * @author gongyu <yin.tong@perfma.com>
 */
public interface Node {

    /**
     * Gets the current plug-in context
     *
     * @return
     */
    PluginBaseInfo getPluginContext();

    /**
     * Get command name
     *
     * @return
     */
    String getCmd();

    /**
     * Get a list of parameters
     *
     * @return
     */
    String[] getArgs();

}
