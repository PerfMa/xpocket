package com.perfma.xlab.xpocket.framework.spi.impl.mxbean;

import com.perfma.xlab.xpocket.command.AbstractSystemCommand;
import com.perfma.xlab.xpocket.framework.spi.execution.pipeline.DefaultXPocketProcess;
import com.perfma.xlab.xpocket.framework.spi.impl.DefaultCommandContext;
import com.perfma.xlab.xpocket.framework.spi.impl.DefaultPluginContext;
import com.perfma.xlab.xpocket.framework.spi.impl.XPocketStatusContext;
import com.perfma.xlab.xpocket.framework.spi.impl.agent.XPocketAgentShellProvider;
import com.perfma.xlab.xpocket.plugin.context.FrameworkPluginContext;
import com.perfma.xlab.xpocket.plugin.manager.CommandManager;
import com.perfma.xlab.xpocket.plugin.manager.PluginManager;
import com.perfma.xlab.xpocket.plugin.ui.UIEngine;
import com.perfma.xlab.xpocket.spi.command.CommandInfo;
import com.perfma.xlab.xpocket.spi.command.XPocketCommand;
import com.perfma.xlab.xpocket.utils.JarUtils;
import com.perfma.xlab.xpocket.utils.TerminalUtil;
import com.perfma.xlab.xpocket.utils.XPocketCommandHelper;
import com.perfma.xlab.xpocket.utils.XPocketConstants;
import java.lang.instrument.Instrumentation;
import java.lang.management.ManagementFactory;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.LockSupport;
import javax.management.MBeanServer;
import javax.management.ObjectName;
import javax.management.remote.JMXConnectorServer;
import javax.management.remote.JMXConnectorServerFactory;
import javax.management.remote.JMXServiceURL;
import org.jline.builtins.telnet.Telnet;
import org.jline.terminal.Terminal;
import org.jline.terminal.TerminalBuilder;

/**
 *
 * @author gongyu <yin.tong@perfma.com>
 */
public class MXBeanUIEngineImpl extends MXBeanNamedObject implements UIEngine {

    private static final String DEFAULT_MXBEAN_PORT = "9528";

    @Override
    public void start(String[] def, String[] args, Instrumentation inst) {

        try {
            String port = DEFAULT_MXBEAN_PORT;
            for (int i = 0; i < args.length; i++) {
                String arg = args[i];
                switch (arg) {
                    case "-port":
                        port = args[++i];
                        break;
                }
            }

            Process p = Runtime.getRuntime().exec(String.format("%s/bin/rmiregistry %s", XPocketConstants.JAVA_HOME, port));
            Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {
                @Override
                public void run() {
                    System.out.println("rmiregistry killed!");
                    p.destroyForcibly();
                }
            }));

            LockSupport.parkNanos(TimeUnit.NANOSECONDS.convert(1, TimeUnit.SECONDS));
            
            DefaultXPocketProcess systemProcess = new DefaultXPocketProcess("XPocket", null);
            systemProcess.setOutputStream(System.out);
            xpocketInit(def[0], systemProcess);
            XPocketCommandHelper.registrySystemProcessAndReader(systemProcess, null);

            MBeanServer server = ManagementFactory.getPlatformMBeanServer();
            XPocketMXBeanServiceImpl xpocket = new XPocketMXBeanServiceImpl();
            ObjectName xpocketObjectName = new ObjectName("XPocket:name=xpocket");

            server.registerMBean(xpocket, xpocketObjectName);

            JMXServiceURL url = new JMXServiceURL(String.format("service:jmx:rmi:///jndi/rmi://localhost:%s/server", port));

            JMXConnectorServer cs = JMXConnectorServerFactory.newJMXConnectorServer(url, null, server);
            cs.start();

        } catch (Throwable ex) {
            ex.printStackTrace();
        }
    }

    private DefaultPluginContext initSystemPlugin() {
        DefaultPluginContext sysPluginContext = new DefaultPluginContext();
        sysPluginContext.setName(XPocketConstants.SYSTEM_PLUGIN_NAME);
        sysPluginContext.setNamespace(XPocketConstants.SYSTEM_PLUGIN_NS);
        String tips = "  1.you can use the \"plugins\" command to list available plugins" + TerminalUtil.lineSeparator
                + "  2.you can use \"help\" to see the detailed usage of the command" + TerminalUtil.lineSeparator
                + "  3.You can use the commands supported by the current operating system itself."
                + " For example, under linux, "
                + "you can use the \"ps\" command to list all processes，"
                + "where the \"ps\" command and the \"xpocket.ps\" command are equivalent\n";
        sysPluginContext.setTips(tips);

        HashMap<String, DefaultCommandContext> cmdMap = new HashMap<>();

        try {
            List<String> classes = JarUtils.findClassesInJarPackage(XPocketConstants.XPOCKET_COMMAND_PACKAGE);
            for (String clazz : classes) {
                Class pluginClass = Class.forName(clazz);
                XPocketCommand commandObject
                        = (XPocketCommand) pluginClass.getConstructor().newInstance();
                ((AbstractSystemCommand) commandObject).setReader(null);

                //collect commandinfo information
                CommandInfo[] infos
                        = (CommandInfo[]) pluginClass
                                .getAnnotationsByType(CommandInfo.class);
                for (CommandInfo info : infos) {
                    cmdMap.put(info.name(),
                            new DefaultCommandContext(info.name(), info.shortName(), info.usage(), info.index(),
                                    commandObject));
                }
            }
        } catch (Throwable ex) {
            ex.printStackTrace();
        }

        sysPluginContext.setCommands(cmdMap);
        return sysPluginContext;
    }

    private void xpocketInit(String def, DefaultXPocketProcess systemProcess) {
        TerminalUtil.printStart(systemProcess);

        // 初始化 System plugin
        DefaultPluginContext sysPluginContext = initSystemPlugin();
        PluginManager.addPlugin(sysPluginContext);

        // 加载默认实现的插件
        PluginManager.loadPlugins(def);

        // 注册命令
        Set<FrameworkPluginContext> plugins = PluginManager.getAvailablePlugins();
        CommandManager.addCommand(plugins);

        //切换到系统插件
        FrameworkPluginContext pluginContext = PluginManager.getPlugin(XPocketConstants.SYSTEM_PLUGIN_NAME, XPocketConstants.SYSTEM_PLUGIN_NS);
        pluginContext.init(systemProcess);
        XPocketStatusContext.open(pluginContext, systemProcess);
    }
}
