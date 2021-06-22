package com.perfma.xlab.xpocket.spi.context;

import com.perfma.xlab.xpocket.spi.XPocketPlugin;
import com.perfma.xlab.xpocket.spi.command.XPocketCommand;
import com.perfma.xlab.xpocket.spi.process.XPocketProcess;

import java.util.Set;

/**
 * @author gongyu <yin.tong@perfma.com>
 */
public interface PluginBaseInfo {

    /**
     * plugin name
     *
     * @return
     */
    String getName();

    /**
     * Name Space
     *
     * @return
     */
    String getNamespace();

    /**
     * get plugin description
     *
     * @return
     */
    String getDescription();

    /**
     * some tips for the plugin
     *
     * @return
     */
    String getTips();

    /**
     * get pluginType
     *
     * @return
     */
    PluginType getType();

    /**
     * @return
     */
    Set<String> getDependencies();

    /**
     * get commandNames for current plugin
     *
     * @return
     */
    Set<String> getCommands();

    /**
     * get CommandBaseInfos for current plugin
     *
     * @return
     */
    Set<CommandBaseInfo> getCommandContexts();

    /**
     * get CommandBaseInfo
     *
     * @param cmd
     * @return
     */
    CommandBaseInfo getCommandContext(String cmd);

    /**
     * @return
     */
    Class getPluginClass();

    /**
     * get current Plugin
     *
     * @param process
     * @return
     */
    XPocketPlugin getPlugin(XPocketProcess process);

    /**
     * get command instance
     *
     * @param cmd
     * @return
     */
    XPocketCommand getCommand(String cmd);

    /**
     * get information about the plug-in
     *
     * @return
     */
    String getPluginInfo();
    
    /**
     * get ascii logo of this plug-in
     * @return 
     */
    String getLogo();
}
