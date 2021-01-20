package com.perfma.xlab.xpocket.plugin.command;

import com.perfma.xlab.xpocket.plugin.base.NamedObject;
import com.perfma.xlab.xpocket.plugin.context.FrameworkPluginContext;

import java.util.Map;
import java.util.Set;

/**
 * @author gongyu <yin.tong@perfma.com>
 */
public interface CommandLoader extends NamedObject {

    /**
     * Gets the correspondence between the plug-in and the command list
     * key   ---  pluginName@pluginNameSpace
     * value ---  command list
     *
     * @return
     */
    Map<String, Set<String>> getCmdTable();

    /**
     * Get the corresponding plug-in context through the command
     *
     * @param command
     * @return
     */
    FrameworkPluginContext findByCommand(String command);

    /**
     * Add all commands corresponding to the plug-in list to the custom cache
     *
     * @param contexts
     * @return
     */
    boolean addCommand(Set<FrameworkPluginContext> contexts);

    /**
     * Returns a collection of plug-in names for all commands
     *
     * @return
     */
    Set<String> cmdSet();

}
