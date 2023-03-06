package com.perfma.xlab.xpocket.framework.spi.impl;

import com.perfma.xlab.xpocket.command.AbstractSystemCommand;
import com.perfma.xlab.xpocket.completer.CommandCompleter;
import com.perfma.xlab.xpocket.completer.GroupStringCompleter;
import com.perfma.xlab.xpocket.completer.ParamsCompleter;
import com.perfma.xlab.xpocket.console.EndOfInputException;
import com.perfma.xlab.xpocket.framework.spi.execution.pipeline.DefaultXPocketProcess;
import com.perfma.xlab.xpocket.linereader.XPocketLineReader;
import com.perfma.xlab.xpocket.plugin.context.FrameworkPluginContext;
import com.perfma.xlab.xpocket.plugin.manager.CommandManager;
import com.perfma.xlab.xpocket.plugin.manager.PluginManager;
import com.perfma.xlab.xpocket.spi.command.CommandInfo;
import com.perfma.xlab.xpocket.spi.command.XPocketCommand;
import com.perfma.xlab.xpocket.plugin.ui.UIEngine;
import com.perfma.xlab.xpocket.utils.JarUtils;
import com.perfma.xlab.xpocket.utils.TerminalUtil;
import com.perfma.xlab.xpocket.utils.XPocketCommandHelper;
import com.perfma.xlab.xpocket.utils.XPocketConstants;
import org.jline.reader.*;
import org.jline.reader.impl.completer.AggregateCompleter;
import org.jline.reader.impl.completer.NullCompleter;
import org.jline.reader.impl.history.DefaultHistory;
import org.jline.terminal.Terminal;
import org.jline.terminal.Terminal.SignalHandler;
import org.jline.terminal.TerminalBuilder;

import java.util.*;
import org.jline.reader.impl.DefaultParser;

/**
 * @author gongyu <yin.tong@perfma.com>
 */
public class DefaultUIEngine extends DefaultNamedObject implements UIEngine {

    protected Parser parser = new DefaultParser();
    private XPocketLineReader reader;

    @Override
    public void start(String def, String[] args) {
        DefaultXPocketProcess systemProcess = new DefaultXPocketProcess("XPocket", null);
        systemProcess.setOutputStream(System.out);
        try {
            Terminal terminal = TerminalBuilder.builder()
                    .nativeSignals(true)
                    .signalHandler(SignalHandler.SIG_IGN)
                    .dumb(true).build();
            reader = new XPocketLineReader(terminal);
            reader.variable(LineReader.HISTORY_FILE, XPocketConstants.PATH + ".history");
            xpocketInit(def, systemProcess);
            CommandProcessor.init(systemProcess);
            List<Completer> completers = new LinkedList<>();
            ParamsCompleter paramsCompleter = new ParamsCompleter();
            registryParamCompleter(paramsCompleter);
            completers.add(buildCommandCompleter());
            completers.add(paramsCompleter);
            completers.add(new NullCompleter());
            reader.setCompleter(new AggregateCompleter(completers));
            reader.setHistory(new DefaultHistory(reader));
            XPocketCommandHelper.registrySystemProcessAndReader(systemProcess, reader);
            for (;;) {
                try {
                    waitForCommands(systemProcess);
                } catch (UserInterruptException ex) {
                    if (doubleCheckCtrlCQuit()) {
                        systemProcess.output("\nBye.");
                        break;
                    }
                }
            }
        } catch (EndOfInputException ex) {
            systemProcess.output("\nBye.");
        } catch (Throwable ex) {
            ex.printStackTrace();
        }
        System.exit(0);
    }

    /**
     * wait For Command
     *
     * @throws EndOfInputException
     */
    private void waitForCommands(DefaultXPocketProcess systemProcess) throws EndOfInputException {
        for (;;) {
            String line = TerminalUtil.readLine(reader, XPocketStatusContext.instance.line());
            if (line == null) {
                break;
            }
            if ("".equalsIgnoreCase(line.trim())) {
                continue;
            }

            switch (line.trim()) {
                case "clear":
                case "clr":
                    reader.clearScreen();
                    continue;
                case "quit":
                case "q":
                    throw new EndOfInputException();
                default:
                    break;
            }
            
            try {
                XPocketCommandHelper.exec(line, systemProcess, reader);
            } catch (EndOfInputException ex) {
                throw ex;
            } 
        }
        throw new EndOfInputException();
    }


    /**
     * Initialize some commonly used
     *
     * @param def
     */
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
        if (!TerminalUtil.SIMPLE_MODE) {
            TerminalUtil.printHelp(systemProcess, pluginContext);
        }
        TerminalUtil.printTail(systemProcess, null);
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
                ((AbstractSystemCommand) commandObject).setReader(reader);

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

    private boolean doubleCheckCtrlCQuit() {
        try {
            String line = reader.readLine("Quit XPocket? (N/Y,Default:N) : ");
            return "y".equalsIgnoreCase(line);
        } catch (UserInterruptException ex) {
            return true;
        }
    }

    private void registryParamCompleter(ParamsCompleter paramsCompleter) {
        Set<String> plugins = new TreeSet<>();
        PluginManager.getAllPlugins().forEach(plugin
                -> plugins.add(
                        String.format("%s@%s", plugin.getName(),
                                plugin.getNamespace().toUpperCase())));
        GroupStringCompleter pluginCompleter = new GroupStringCompleter(plugins, "PLUGIN");
        paramsCompleter.registry(XPocketConstants.SYSTEM_PLUGIN_NAME + ".use@" + XPocketConstants.SYSTEM_PLUGIN_NS.toUpperCase(), pluginCompleter);
        paramsCompleter.registry(XPocketConstants.SYSTEM_PLUGIN_NAME + ".use", pluginCompleter);
        paramsCompleter.registry("use", pluginCompleter);

        GroupStringCompleter cmdCompleter = new GroupStringCompleter(getCommandCompleterWithPlugin());
        AggregateCompleter helpCompleter = new AggregateCompleter(pluginCompleter, cmdCompleter);

        paramsCompleter.registry(XPocketConstants.SYSTEM_PLUGIN_NAME + ".help@" + XPocketConstants.SYSTEM_PLUGIN_NS.toUpperCase(), helpCompleter);
        paramsCompleter.registry(XPocketConstants.SYSTEM_PLUGIN_NAME + ".help", helpCompleter);
        paramsCompleter.registry("help", helpCompleter);
        paramsCompleter.registry("h", helpCompleter);

    }

    private Set<Candidate> getCommandCompleterWithPlugin() {
        Set<String> allCommand = CommandManager.cmdSet();
        Set<Candidate> commands = new TreeSet<>();
        commands.add(new Candidate("clear", "clear", "GLOBAL", null, null, null, true));
        commands.add(new Candidate("quit", "quit", "GLOBAL", null, null, null, true));
        for (String cmd : allCommand) {
            if (cmd.contains(".")
                    && cmd.contains("@")
                    && (!cmd.startsWith("system.clear") && !cmd.startsWith("system.quit"))) {
                String value = cmd.substring(0, cmd.indexOf("@"));
                String group = cmd.substring(0, cmd.indexOf(".")) + cmd.substring(cmd.indexOf("@"), cmd.length()).toUpperCase();
                commands.add(new Candidate(value, value, "PLUGIN_NAME : " + group, null, null, null, true));
            }
        }
        //填充由经验积累得出的解决方案别名
        for (String cmd : CommandProcessor.aliases) {
            commands.add(new Candidate(cmd, cmd, "COMMAND_PROCESS", null, null, null, true));
        }
        return commands;
    }

    private Completer buildCommandCompleter() {
        return new CommandCompleter(getCommandCompleterWithPlugin());
    }

}
