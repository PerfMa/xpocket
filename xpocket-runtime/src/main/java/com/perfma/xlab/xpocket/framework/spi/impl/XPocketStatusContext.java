package com.perfma.xlab.xpocket.framework.spi.impl;

import com.perfma.xlab.xpocket.plugin.context.FrameworkPluginContext;
import com.perfma.xlab.xpocket.spi.context.SessionContext;
import com.perfma.xlab.xpocket.spi.process.XPocketProcess;
import com.perfma.xlab.xpocket.utils.TerminalUtil;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.fusesource.jansi.Ansi;
import com.perfma.xlab.xpocket.spi.context.PluginBaseInfo;
import com.perfma.xlab.xpocket.spi.context.CommandBaseInfo;


/**
 * @author: TuGai
 **/
public class XPocketStatusContext implements SessionContext {

    public static volatile XPocketStatusContext instance;

    private static final Map<PluginBaseInfo, XPocketStatusContext> sessions = new ConcurrentHashMap<>();

    private Integer pid = -1;

    private FrameworkPluginContext pluginContext;

    private CommandBaseInfo currentCommand;

    private String networkInfo;

    private final Map<String, String> props = new ConcurrentHashMap<>();

    private XPocketStatusContext() {
    }

    public static void open(FrameworkPluginContext context, XPocketProcess process) {
        if (!sessions.containsKey(context)) {
            XPocketStatusContext sessionContext = new XPocketStatusContext();
            sessionContext.pluginContext = context;
            sessions.putIfAbsent(context, sessionContext);
        }

        if (instance != null && instance.pluginContext.getPlugin(process) != null) {
            instance.pluginContext.getPlugin(process).switchOff(instance);
        }

        instance = sessions.get(context);
        instance.pluginContext.init(process);
        if (instance != null && instance.pluginContext.getPlugin(process) != null) {
            instance.pluginContext.getPlugin(process).printLogo(process);
            process.output(TerminalUtil.lineSeparator);
            instance.pluginContext.getPlugin(process).switchOn(instance);
        }
    }

    public String line() {
        String start = Ansi.ansi().fg(Ansi.Color.RED).a("XPocket [").reset().toString();
        String colon = Ansi.ansi().fg(Ansi.Color.RED).a(": ").reset().toString();
        String end = Ansi.ansi().fg(Ansi.Color.RED).a("] > ").reset().toString();
        StringBuilder sb = new StringBuilder();
        sb.append(start);
        if (pluginContext != null) {
            String name = pluginContext.getName();
            name = Ansi.ansi().fg(Ansi.Color.YELLOW).a(name).reset().toString();
            sb.append(name);
            if (pid >= 0) {
                sb.append(colon).append(pid).append(end);
            } else {
                sb.append(end);
            }
        }
        return sb.toString();
    }

    @Override
    public int pid() {
        return pid;
    }

    @Override
    public void setPid(int pid) {
        this.pid = pid;
    }

    @Override
    public PluginBaseInfo currentPlugin() {
        return pluginContext;
    }

    @Override
    public CommandBaseInfo currentCommand() {
        return currentCommand;
    }

    @Override
    public String networkInfo() {
        return networkInfo;
    }

    @Override
    public void setNetworkInfo(String networkInfo) {
        this.networkInfo = networkInfo;
    }

    @Override
    public void setProp(String key, String value) {
        props.put(key, value);
    }

    @Override
    public String getProp(String key) {
        return props.get(key);
    }

}
