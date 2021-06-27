package com.perfma.xlab.xpocket.framework.spi.impl;

import com.perfma.xlab.xpocket.plugin.context.FrameworkPluginContext;
import com.perfma.xlab.xpocket.spi.XPocketAgentPlugin;
import com.perfma.xlab.xpocket.spi.XPocketPlugin;
import com.perfma.xlab.xpocket.spi.command.XPocketCommand;
import com.perfma.xlab.xpocket.spi.context.CommandBaseInfo;
import com.perfma.xlab.xpocket.spi.context.PluginType;
import com.perfma.xlab.xpocket.spi.process.XPocketProcess;
import com.perfma.xlab.xpocket.utils.AsciiArtUtil;
import java.lang.instrument.Instrumentation;

import java.util.*;
import java.util.Map.Entry;

/**
 * @author gongyu <yin.tong@perfma.com>
 */
public class DefaultPluginContext implements FrameworkPluginContext {

    private String logo;
    
    private String name;

    private String namespace;

    private String description;

    private String tips;

    private String pluginInfo;

    private PluginType type;

    private Set<String> dependencies;

    private Map<String, DefaultCommandContext> commands;

    private SortedSet<CommandBaseInfo> orderedContexts;

    private Class pluginClass;

    private XPocketPlugin plugin;

    private boolean inited = false;
    
    private Instrumentation inst;
    
    private boolean isOnLoad = false;
    
    @Override
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String getNamespace() {
        return namespace;
    }

    public void setNamespace(String namespace) {
        this.namespace = namespace;
    }

    public void setTips(String tips) {
        this.tips = tips;
    }

    @Override
    public PluginType getType() {
        return type;
    }

    public void setType(PluginType type) {
        this.type = type;
    }

    @Override
    public Set<String> getDependencies() {
        return dependencies;
    }

    public void setDependencies(Set<String> dependencies) {
        this.dependencies = dependencies;
    }

    @Override
    public Set<String> getCommands() {
        return commands.keySet();
    }

    public void setCommands(Map<String, DefaultCommandContext> cmds) {
        this.commands = new HashMap<>();
        
        for(Entry<String,DefaultCommandContext> e : cmds.entrySet()) {
            String key = e.getKey();
            DefaultCommandContext value = e.getValue();
            this.commands.put(key,value);
            if(value.shortName() != null) {
                this.commands.put(value.shortName(),value);
            }
        }
        
        this.orderedContexts = new TreeSet<>(
                (CommandBaseInfo o1, CommandBaseInfo o2) -> {
                    return (o1.index() - o2.index() == 0)
                            ? -1
                            : (o1.index() - o2.index());
                });
        orderedContexts.addAll(cmds.values());
    }

    @Override
    public Class getPluginClass() {
        return pluginClass;
    }

    public void setPluginClass(Class pluginClass) {
        this.pluginClass = pluginClass;
    }

    @Override
    public XPocketCommand getCommand(String cmd) {
        return commands.containsKey(cmd) ? commands.get(cmd).instance() : null;
    }

    @Override
    public XPocketPlugin getPlugin(XPocketProcess process) {
        if (!inited && pluginClass != null) {
            doInit(process);
        }
        return plugin;
    }

    public void setInst(Instrumentation inst) {
        this.inst = inst;
    }

    public void setIsOnLoad(boolean isOnLoad) {
        this.isOnLoad = isOnLoad;
    }
    
    @Override
    public void init(XPocketProcess process) {
        if (!inited && pluginClass != null) {
            doInit(process);
        }
    }

    private synchronized void doInit(XPocketProcess process) {
        if (!inited) {
            ClassLoader cl = Thread.currentThread().getContextClassLoader();
            try {
                Thread.currentThread().setContextClassLoader(pluginClass.getClassLoader());
                plugin = (XPocketPlugin) pluginClass.getDeclaredConstructor().newInstance();
                plugin.init(process);
                
                if(PluginType.JAVA_AGENT == type && inst != null) {
                    ((XPocketAgentPlugin)plugin).init(process, inst, isOnLoad);
                }
                
                logo = plugin.logo();
                
                if(logo == null) {
                    StringBuilder text = new StringBuilder(name.length() * 2);
                    
                    for(char c : name.toUpperCase().toCharArray()) {
                        text.append(c).append(" ");
                    }
                    
                    logo = AsciiArtUtil.text2AsciiArt(text.toString());
                }
                
                for (DefaultCommandContext ctx : commands.values()) {
                    ctx.instance().init(plugin);
                }

                inited = true;
            } catch (Throwable ex) {
                ex.printStackTrace();
            } finally {
                Thread.currentThread().setContextClassLoader(cl);
            }
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        DefaultPluginContext that = (DefaultPluginContext) o;
        return Objects.equals(name, that.name) &&
                Objects.equals(namespace, that.namespace);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, namespace);
    }

    @Override
    public Set<CommandBaseInfo> getCommandContexts() {
        return this.orderedContexts;
    }

    @Override
    public CommandBaseInfo getCommandContext(String cmd) {
        return commands.get(cmd);
    }

    @Override
    public String getTips() {
        return tips;
    }

    public void setPluginInfo(String pluginInfo) {
        this.pluginInfo = pluginInfo;
    }

    @Override
    public String getPluginInfo() {
        return pluginInfo;
    }

    public void setLogo(String logo) {
        this.logo = logo;
    }
    
    @Override
    public String getLogo() {
        return logo;
    }

}
