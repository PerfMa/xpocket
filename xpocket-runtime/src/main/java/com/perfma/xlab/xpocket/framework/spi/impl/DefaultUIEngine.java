package com.perfma.xlab.xpocket.framework.spi.impl;

import com.perfma.xlab.xpocket.command.impl.AbstractSystemCommand;
import com.perfma.xlab.xpocket.completer.CommandCompleter;
import com.perfma.xlab.xpocket.completer.GroupStringCompleter;
import com.perfma.xlab.xpocket.completer.ParamsCompleter;
import com.perfma.xlab.xpocket.console.EndOfInputException;
import com.perfma.xlab.xpocket.framework.spi.execution.pipeline.DefaultNode;
import com.perfma.xlab.xpocket.framework.spi.execution.pipeline.DefaultProcessInfo;
import com.perfma.xlab.xpocket.framework.spi.execution.pipeline.DefaultXPocketProcess;
import com.perfma.xlab.xpocket.linereader.XPocketLineReader;
import com.perfma.xlab.xpocket.plugin.context.FrameworkPluginContext;
import com.perfma.xlab.xpocket.plugin.manager.CommandManager;
import com.perfma.xlab.xpocket.plugin.manager.ExecutionManager;
import com.perfma.xlab.xpocket.plugin.manager.PluginManager;
import com.perfma.xlab.xpocket.spi.command.CommandInfo;
import com.perfma.xlab.xpocket.spi.command.XPocketCommand;
import com.perfma.xlab.xpocket.plugin.ui.UIEngine;
import com.perfma.xlab.xpocket.utils.LogoPrinter;
import com.perfma.xlab.xpocket.utils.TerminalUtil;
import com.perfma.xlab.xpocket.utils.XPocketConstants;
import com.sun.tools.attach.AgentLoadException;
import org.jline.reader.*;
import org.jline.reader.impl.completer.AggregateCompleter;
import org.jline.reader.impl.completer.NullCompleter;
import org.jline.reader.impl.history.DefaultHistory;
import org.jline.terminal.Terminal;
import org.jline.terminal.Terminal.SignalHandler;
import org.jline.terminal.TerminalBuilder;

import java.io.IOException;
import java.util.*;

/**
 * @author gongyu <yin.tong@perfma.com>
 */
public class DefaultUIEngine extends DefaultNamedObject implements UIEngine {
    
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
            for (; ; ) {
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
        for (; ; ) {
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
                DefaultProcessInfo[] infos = null;
                if (CommandProcessor.solutionMap.containsKey(line.trim())) {
                    infos = new DefaultProcessInfo[]{CommandProcessor.solutionMap.get(line.trim())};
                } else {
                    infos = buildProcessInfo(line, systemProcess);
                }

                if (infos != null) {
                    for (DefaultProcessInfo info : infos) {
                        TerminalUtil.printHeader(systemProcess, info);
                        ExecutionManager.invoke(info, reader);
                        TerminalUtil.printTail(systemProcess, info);
                    }
                }
            } catch (AgentLoadException | IOException ex) {
                systemProcess.output("Please check your jdk version both used to run XPocket and target processor! They must be equal!");
            } catch (EndOfInputException ex) {
                throw ex;
            } catch (Throwable ex) { // command execution problem
                ex.printStackTrace();
                systemProcess.output("command execution has problem");
            }
        }
        throw new EndOfInputException();
    }

    /**
     * encapsulation cmd，conversion to DefaultProcessInfo
     *
     * @param line
     * @param systemProcess
     */
    private DefaultProcessInfo[] buildProcessInfo(String line, DefaultXPocketProcess systemProcess) {
        // 当前 plugin 上下文
        FrameworkPluginContext pluginContext = (FrameworkPluginContext) XPocketStatusContext.instance.currentPlugin();
        if (pluginContext == null) {
            pluginContext = PluginManager.getPlugin(XPocketConstants.SYSTEM_PLUGIN_NAME, XPocketConstants.SYSTEM_PLUGIN_NS);
        }
        pluginContext.init(systemProcess);

        //  1、解析命令
        // 命令： pluginName1.cmd1 args | pluginName2.cmd2 args
        // 或者： cmd1 args | pluginName2.cmd2 args
        String oldCmd = line.trim();
        // 先解析管道符
        String[][] pipelineCmd = parsePipeline(oldCmd);
        DefaultProcessInfo[] infos = new DefaultProcessInfo[pipelineCmd.length];

        for (int k = 0; k < pipelineCmd.length; k++) {
            String[] pipelineCmdUnit = pipelineCmd[k];
            DefaultProcessInfo info = new DefaultProcessInfo();
            infos[k] = info;
            info.setOutput(systemProcess.getOutputStream());
            // 解析各个管道符中的命令
            for (String cmdAndArg : pipelineCmdUnit) {
                cmdAndArg = cmdAndArg.trim();
                ParsedLine singleLine = reader.getParser().parse(cmdAndArg, 0);
                String cmd = singleLine.words().get(0);
                String[] args = new String[singleLine.words().size() - 1];

                for (int j = 0; j < args.length; j++) {
                    args[j] = singleLine.words().get(j + 1);
                }

                // 判断命令中是否包含插件名
                int index = cmd.indexOf(".");

                FrameworkPluginContext selected = null;
                String selectedCmd = cmd;
                if (index > 0) {
                    FrameworkPluginContext context = CommandManager.findByCommand(cmd);
                    if (context == null) {
                        systemProcess.output("can not find cmd :" + cmd + ".........");
                        return null;
                    }
                    selected = context;
                    selectedCmd = cmd.substring(index + 1);
                } else {
                    if (pluginContext.getCommands().contains(cmd)) {
                        selected = pluginContext;
                    } else if ((selected = CommandManager.findByCommand(cmd)) != null) {
                        //NOTHING NEED TO DO
                    } else {
                        selected = PluginManager.getPlugin(XPocketConstants.SYSTEM_PLUGIN_NAME, XPocketConstants.SYSTEM_PLUGIN_NS);
                    }
                }
                selected.init(systemProcess);
                //  2、构建命令执行 pipline
                info.addNode(new DefaultNode(selected, selectedCmd, args));
            }
        }
        return infos;
    }

    /**
     * Initialize some commonly used
     *
     * @param def
     */
    private void xpocketInit(String def, DefaultXPocketProcess systemProcess) {
        systemProcess.output("@|green *** Welcome to XPocket-Cli|@ @|green ***|@");
        systemProcess.output(TerminalUtil.lineSeparator);
        LogoPrinter.print(systemProcess);
        systemProcess.output(TerminalUtil.lineSeparator);
        systemProcess.output("@|green Initiator : PerfMa |@");
        systemProcess.output("@|green version   : " + XPocketConstants.VERSION + " |@");
        systemProcess.output("@|green site      : " + XPocketConstants.CLUB + " |@");
        systemProcess.output("@|green github    : " + XPocketConstants.GITHUB + " |@");
        systemProcess.output(TerminalUtil.lineSeparator);

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
        TerminalUtil.printHelp(systemProcess, pluginContext);
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
        for (String cmd : XPocketConstants.XPOCKET_COMMANDS) {
            try {
                Class pluginClass = Class.forName(XPocketConstants.XPOCKET_COMMAND_PACKAGE + cmd
                        + "Command");
                XPocketCommand commandObject
                        = (XPocketCommand) pluginClass.getConstructor().newInstance();
                ((AbstractSystemCommand) commandObject).setReader(reader);

                //collect commandinfo information
                CommandInfo[] infos
                        = (CommandInfo[]) pluginClass
                        .getAnnotationsByType(CommandInfo.class);
                for (CommandInfo info : infos) {
                    cmdMap.put(info.name(),
                            new DefaultCommandContext(info.name(),info.shortName(), info.usage(), info.index(),
                                    commandObject));
                }
            } catch (Throwable ex) {
                //ignore
            }
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

    private String[][] parsePipeline(String line) {
        List<List<String>> cmds = new ArrayList<>();
        List<String> pipeline = new ArrayList<>();
        cmds.add(pipeline);

        char[] src = line.toCharArray();

        StringBuilder builder = new StringBuilder();
        boolean isQuoteOpen = false;

        for (int i = 0; i < src.length; i++) {

            char c = src[i];

            switch (c) {
                case '"':
                    isQuoteOpen = !isQuoteOpen;
                    builder.append(c);
                    break;
                case '&':
                    if (isQuoteOpen) {
                        builder.append(c);
                    } else {
                        pipeline.add(builder.substring(0, builder.length()));
                        builder = new StringBuilder();
                        pipeline = new ArrayList<>();
                        cmds.add(pipeline);
                    }
                    break;
                case '|':
                    if (isQuoteOpen) {
                        builder.append(c);
                    } else {
                        pipeline.add(builder.substring(0, builder.length()));
                        builder = new StringBuilder();
                    }
                    break;
                default:
                    builder.append(c);
            }

        }

        if (builder.length() > 0) {
            pipeline.add(builder.substring(0, builder.length()));
        }

        String[][] result = new String[cmds.size()][];

        for (int i = 0; i < cmds.size(); i++) {
            List<String> pipe = cmds.get(i);
            String[] pipeArray = new String[pipe.size()];
            pipe.toArray(pipeArray);
            result[i] = pipeArray;
        }

        return result;
    }

}
