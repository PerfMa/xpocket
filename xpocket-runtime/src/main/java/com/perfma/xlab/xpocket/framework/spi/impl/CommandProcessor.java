package com.perfma.xlab.xpocket.framework.spi.impl;

import com.perfma.xlab.xpocket.framework.spi.execution.pipeline.DefaultNode;
import com.perfma.xlab.xpocket.framework.spi.execution.pipeline.DefaultProcessInfo;
import com.perfma.xlab.xpocket.framework.spi.execution.pipeline.DefaultXPocketProcess;
import com.perfma.xlab.xpocket.plugin.context.FrameworkPluginContext;
import com.perfma.xlab.xpocket.plugin.manager.CommandManager;
import com.perfma.xlab.xpocket.plugin.manager.PluginManager;
import com.perfma.xlab.xpocket.utils.XPocketConstants;
import java.io.File;

import java.io.FileInputStream;
import java.util.*;

/**
 * 用于加载那些解决问题沉淀下来的可执行命令，并以别名的方式记录
 *
 * @author xinxian
 * @create 2020-11-11 20:22
 **/
public class CommandProcessor {

    public static Map<String, DefaultProcessInfo> solutionMap = new HashMap<>();

    public static Set<String> aliases;

    public static String RESOURCE = System.getProperty("XPOCKET_CONFIG_PATH") + "process.properties";

    public static void init(DefaultXPocketProcess process) {
        aliases = new HashSet<>();
        File resourceFile = new File(RESOURCE);
        
        try {
            if(!resourceFile.exists()) {
                resourceFile.createNewFile();
            }
        } catch (Throwable ex) {
            process.output("ERROR : create process store file failed,cause by : " + ex.getMessage());
            System.exit(1);
        }
        
        try (FileInputStream inputStream = new FileInputStream(resourceFile)) {
            Properties properties = new Properties();
            properties.load(inputStream);
            final Set<Map.Entry<Object, Object>> entries = properties.entrySet();
            for (Map.Entry<Object, Object> entry : entries) {
                final String alias = (String) entry.getKey();
                final String commandLine = (String) entry.getValue();
                final DefaultProcessInfo defaultProcessInfo = buildProcessInfo(commandLine, process);
                aliases.add(alias);
                solutionMap.put(alias, defaultProcessInfo);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static DefaultProcessInfo buildProcessInfo(String commandLine, DefaultXPocketProcess process) {
        //  1、解析命令
        DefaultProcessInfo info = new DefaultProcessInfo();
        info.setOutput(process.getOutputStream());
        // 命令： pluginName1.cmd1 args | pluginName2.cmd2 args
        // 或者： cmd1 args | cmd2 args
        String oldCmd = commandLine.trim();
        // 先解析管道符
        String[] pipelineCmd = oldCmd.split("\\|");
        // 解析各个管道符中的命令
        for (String cmdAndArg : pipelineCmd) {
            cmdAndArg = cmdAndArg.trim();
            int i = cmdAndArg.indexOf(" ");
            String cmd = i != -1 ? cmdAndArg.substring(0, i) : cmdAndArg;
            String args = i != -1 ? cmdAndArg.substring(i).trim() : "";
            // 判断命令中是否包含插件名
            int index = cmd.indexOf(".");
            FrameworkPluginContext selected = null;
            String selectedCmd = cmd;
            if (index > 0) {
                FrameworkPluginContext context = CommandManager.findByCommand(cmd);
                if (context == null) {
                    break;
                }
                selected = context;
                selectedCmd = cmd.substring(index + 1);
            } else {
                final Set<String> cmds = CommandManager.cmdSet();
                for (String item : cmds) {
                    if (item.contains(cmd)) {
                        final FrameworkPluginContext context = CommandManager.findByCommand(cmd);
                        if (context != null) {
                            selected = context;
                            break;
                        }
                    }
                }
                if (selected == null) {
                    selected = PluginManager.getPlugin(XPocketConstants.SYSTEM_PLUGIN_NAME, XPocketConstants.SYSTEM_PLUGIN_NS);
                }
            }
            selected.init(process);
            //  2、构建命令执行 pipline
            info.addNode(new DefaultNode(selected, selectedCmd, args.split(" ")));
        }
        return info;
    }
}
