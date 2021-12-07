package com.perfma.xlab.xpocket.framework.spi.impl.once;

import com.perfma.xlab.xpocket.console.EndOfInputException;
import com.perfma.xlab.xpocket.framework.spi.execution.pipeline.DefaultNode;
import com.perfma.xlab.xpocket.framework.spi.execution.pipeline.DefaultProcessInfo;
import com.perfma.xlab.xpocket.framework.spi.execution.pipeline.DefaultXPocketProcess;
import com.perfma.xlab.xpocket.framework.spi.impl.CommandProcessor;
import com.perfma.xlab.xpocket.framework.spi.impl.DefaultCommandContext;
import com.perfma.xlab.xpocket.framework.spi.impl.DefaultPluginContext;
import com.perfma.xlab.xpocket.framework.spi.impl.XPocketStatusContext;
import com.perfma.xlab.xpocket.plugin.context.FrameworkPluginContext;
import com.perfma.xlab.xpocket.plugin.manager.CommandManager;
import com.perfma.xlab.xpocket.plugin.manager.ExecutionManager;
import com.perfma.xlab.xpocket.plugin.manager.PluginManager;
import com.perfma.xlab.xpocket.spi.command.CommandInfo;
import com.perfma.xlab.xpocket.spi.command.XPocketCommand;
import com.perfma.xlab.xpocket.plugin.ui.UIEngine;
import com.perfma.xlab.xpocket.utils.JarUtils;
import com.perfma.xlab.xpocket.utils.TerminalUtil;
import com.perfma.xlab.xpocket.utils.XPocketConstants;
import com.sun.tools.attach.AgentLoadException;
import org.jline.reader.*;

import java.io.IOException;
import java.util.*;
import org.jline.reader.impl.DefaultParser;

/**
 * @author gongyu <yin.tong@perfma.com>
 */
public class OnceExecutionUIEngineImpl extends OnceNamedObject implements UIEngine {

    protected Parser parser = new DefaultParser();
    
    @Override
    public void start(String def, String[] args) {
        DefaultXPocketProcess systemProcess = new DefaultXPocketProcess("XPocket", null);
        systemProcess.setOutputStream(System.out);
        try {
            xpocketInit(def, systemProcess);
            CommandProcessor.init(systemProcess);
            executeCommand(systemProcess,args);
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
    private void executeCommand(DefaultXPocketProcess systemProcess,String[] args) throws EndOfInputException {
            String line = args[0];
            if (line == null || "".equalsIgnoreCase(line.trim())) {
                return ;
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
                        ExecutionManager.invoke(info, null);
                    }
                }
            } catch (AgentLoadException | IOException ex) {
                systemProcess.output("Please check your jdk version both used to run XPocket and target processor! They must be equal!");
            } catch (Throwable ex) { // command execution problem
                systemProcess.output("command execution has problem");
            }
        
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
                ParsedLine singleLine = parser.parse(cmdAndArg, 0);
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
            //ignore
        }

        sysPluginContext.setCommands(cmdMap);
        return sysPluginContext;
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
