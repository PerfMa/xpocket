package com.perfma.xlab.xpocket.utils;

import com.perfma.xlab.xpocket.console.EndOfInputException;
import com.perfma.xlab.xpocket.framework.spi.execution.pipeline.DefaultNode;
import com.perfma.xlab.xpocket.framework.spi.execution.pipeline.DefaultProcessInfo;
import com.perfma.xlab.xpocket.framework.spi.execution.pipeline.DefaultXPocketProcess;
import com.perfma.xlab.xpocket.framework.spi.impl.XPocketStatusContext;
import com.perfma.xlab.xpocket.plugin.context.FrameworkPluginContext;
import com.perfma.xlab.xpocket.plugin.execution.Node;
import com.perfma.xlab.xpocket.plugin.manager.CommandManager;
import com.perfma.xlab.xpocket.plugin.manager.ExecutionManager;
import com.perfma.xlab.xpocket.plugin.manager.PluginManager;
import com.sun.tools.attach.AgentLoadException;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.jline.reader.LineReader;
import org.jline.reader.ParsedLine;
import org.jline.reader.Parser;
import org.jline.reader.impl.DefaultParser;

/**
 *
 * @author gongyu <yin.tong@perfma.com>
 */
public class XPocketCommandHelper {

    private static DefaultXPocketProcess systemProcess;

    private static Parser parser = new DefaultParser();

    public static void registrySystemProcessAndReader(
            DefaultXPocketProcess process, LineReader reader) {
        systemProcess = process;
    }

    public static void println(String content) {
        systemProcess.output(content);
    }

    public static String execByResult(String command) {
        DefaultXPocketProcess executionProcess = new DefaultXPocketProcess("execution", null);
        StringBuilder result = new StringBuilder();
        executionProcess.setOutputStream(new OutputStream() {
            @Override
            public void write(int b) throws IOException {
                throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
            }

            @Override
            public void write(byte[] b, int off, int len) throws IOException {
                result.append(new String(b,off,len));
            }
        });
        
        exec(command,executionProcess,null);
        return result.toString();
    }

    public static void exec(String command) {
        exec(command, systemProcess, null);
    }

    public static void exec(String command, DefaultXPocketProcess process, LineReader reader) {
        
        try {
            DefaultProcessInfo[] infos = buildProcessInfo(command, process);

            if (infos != null) {
                for (DefaultProcessInfo info : infos) {
                    ExecutionManager.invoke(info, reader);
                }
            }
        } catch (AgentLoadException | IOException ex) {
            process.output("Please check your jdk version both used to run XPocket and target processor! They must be equal!");
        } catch (EndOfInputException ex) {
            throw ex;
        } catch (Throwable ex) { // command execution problem
            ex.printStackTrace();
            process.output("command execution has problem");
        }
    }

    /**
     * encapsulation cmd，conversion to DefaultProcessInfo
     *
     * @param line
     * @param systemProcess
     */
    private static DefaultProcessInfo[] buildProcessInfo(String line, DefaultXPocketProcess process) {
        // 当前 plugin 上下文
        FrameworkPluginContext pluginContext = (FrameworkPluginContext) XPocketStatusContext.instance.currentPlugin();
        if (pluginContext == null) {
            pluginContext = PluginManager.getPlugin(XPocketConstants.SYSTEM_PLUGIN_NAME, XPocketConstants.SYSTEM_PLUGIN_NS);
        }
        pluginContext.init(process);

        //  1、解析命令
        // 命令： pluginName1.cmd1 args | pluginName2.cmd2 args
        // 或者： cmd1 args | pluginName2.cmd2 args
        String oldCmd = line.trim();
        
        // 先解析管道符
        List<List<List<String>>> pipelineCmd = parsePipeline(oldCmd);
        DefaultProcessInfo[] infos = new DefaultProcessInfo[pipelineCmd.size()];

        for (int k = 0; k < pipelineCmd.size(); k++) {
            List<List<String>> pipelineCmdUnit = pipelineCmd.get(k);
            DefaultProcessInfo info = new DefaultProcessInfo();
            infos[k] = info;
            info.setOutput(process.getOutputStream());
            // 解析各个管道符中的命令
            for (List<String> cmdAndArg : pipelineCmdUnit) {
                String cmd = cmdAndArg.get(0);
                String[] args = new String[cmdAndArg.size() - 1];

                for (int j = 0; j < args.length; j++) {
                    args[j] = cmdAndArg.get(j + 1);
                }

                // 判断命令中是否包含插件名
                int index = cmd.indexOf(".");

                FrameworkPluginContext selected = null;
                String selectedCmd = cmd;
                if (index > 0) {
                    FrameworkPluginContext context = CommandManager.findByCommand(cmd);
                    if (context == null) {
                        process.output("can not find cmd :" + cmd + ".........");
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
                selected.init(process);
                //  2、构建命令执行 pipline
                info.addNode(new DefaultNode(selected, selectedCmd, args));
            }
        }
        return infos;
    }

    private static List<List<List<String>>> parsePipeline(String line) {
        ParsedLine parserLine = parser.parse(line, 0);
        List<List<List<String>>> result = new ArrayList<>();
        List<List<String>> tempUnit = new ArrayList<>();
        List<String> temp = new ArrayList<>();
        tempUnit.add(temp);
        for (String s : parserLine.words()) {
            switch(s) {
                case "&" :
                    result.add(tempUnit);
                    tempUnit = new ArrayList<>();
                    temp = new ArrayList<>();
                    tempUnit.add(temp);
                    break;
                case "|" :
                    temp = new ArrayList<>();
                    tempUnit.add(temp);
                    break;
                default :
                    temp.add(s);
                    
            }
        }
        
        result.add(tempUnit);

        return result;
    }

    public static void main(String[] args) {
        Parser parser = new DefaultParser();
        ParsedLine line = parser.parse("scroll exec -script \"for(int i=0;i<4;i++){exec(\\\"jps | grep 1\\\");}\" | jps", 0);
        List<String> words = line.words();

        System.out.println(Arrays.toString(words.toArray()));

    }

}
