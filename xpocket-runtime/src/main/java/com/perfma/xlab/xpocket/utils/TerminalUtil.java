package com.perfma.xlab.xpocket.utils;

import com.perfma.xlab.xpocket.framework.spi.execution.pipeline.DefaultProcessInfo;
import com.perfma.xlab.xpocket.framework.spi.impl.XPocketStatusContext;
import com.perfma.xlab.xpocket.plugin.execution.Node;
import com.perfma.xlab.xpocket.spi.context.CommandBaseInfo;
import com.perfma.xlab.xpocket.spi.context.PluginBaseInfo;
import com.perfma.xlab.xpocket.spi.process.XPocketProcess;
import org.jline.reader.impl.LineReaderImpl;

/**
 * @author xinxian
 * @create 2020-09-10 10:35
 */
public class TerminalUtil {

    public static String lineSeparator = System.getProperty("line.separator");

    public static void printHeader(XPocketProcess process, DefaultProcessInfo info) {
        process.output(TerminalUtil.lineSeparator);
        process.output("--------------------------------------------------------------------");

        StringBuilder currentCommand = new StringBuilder();

        for (Node n : info.nodes()) {
            currentCommand.append("@|yellow ").append(n.getPluginContext().getName()).append("|@").append(".");
            currentCommand.append("@|green ").append(n.getCmd()).append("|@").append("@");
            currentCommand.append("@|magenta ").append(n.getPluginContext().getNamespace().toUpperCase()).append("|@");
            currentCommand.append(" ");
            if (n.getArgs() != null && n.getArgs().length > 0) {
                for (String arg : n.getArgs()) {
                    currentCommand.append(arg).append(" ");
                }
            }
            currentCommand.append("| ");
        }

        String cmd = currentCommand.substring(0, currentCommand.lastIndexOf("|"));
        process.output(String.format("Current Execution : %s", cmd));
        process.output(String.format("Current Plugin : @|yellow %s|@@@|magenta %s|@",
                XPocketStatusContext.instance.currentPlugin().getName(),
                XPocketStatusContext.instance.currentPlugin().getNamespace()
                        .toUpperCase()));
        process.output("-------------------------------");
    }

    public static void printTail(XPocketProcess process, DefaultProcessInfo info) {
        process.output(" " + TerminalUtil.lineSeparator);
        if (info != null && info.nodes().size() > 0) {
            Node lastNode = info.nodes().get(info.nodes().size() - 1);
            PluginBaseInfo lastPlugin = lastNode.getPluginContext();
            if (lastPlugin != null && lastPlugin.getCommand(lastNode.getCmd()) != null) {
                String[] tips = lastPlugin.getCommand(lastNode.getCmd()).tips();
                if (tips != null && tips.length > 0) {
                    process.output(" Tips:" + TerminalUtil.lineSeparator);
                    for (int i = 1; i <= tips.length; i++) {
                        process.output("  " + i + ": " + tips[i - 1] + TerminalUtil.lineSeparator);
                    }
                }
            }
        }

        process.output("--------------------------------------------------------------------");
        process.output(TerminalUtil.lineSeparator);
    }


    public static void printHelp(XPocketProcess process, PluginBaseInfo context) {
        final String pluginInfo = context.getPluginInfo();
        if (pluginInfo != null && !pluginInfo.isEmpty()) {
            process.output(pluginInfo);
            process.output(TerminalUtil.lineSeparator);
        }

        if (context.getDescription() != null) {
            process.output(TerminalUtil.lineSeparator);
            process.output(context.getDescription());
            process.output(TerminalUtil.lineSeparator);
        }

        process.output("Command Definitions : ");
        process.output("@|white  " + fillSpace("Command-Name", 24) + " |@     @|white  " + fillSpace("Command-Description", 14) + " |@");

        for (CommandBaseInfo h : context.getCommandContexts()) {
            if (h.instance().isAvailableNow(h.name())) {
                StringBuilder sb = new StringBuilder("  @|green ").append(fillSpace(h.name(),28)).append(" |@").append("  ");
                
                String[] usages = h.usage().split("\n");
                sb.append("@|white ").append(usages[0]).append(" |@");
                sb.append(TerminalUtil.lineSeparator);
                
                for(int i=1;i<usages.length;i++) {
                    sb.append(fillSpace("",33));
                    sb.append("@|white ").append(usages[i]).append(" |@");
                    sb.append(TerminalUtil.lineSeparator);
                }

                process.output(sb.toString());
            }
        }
        String tips = context.getTips();
        if (tips != null && !tips.isEmpty()) {
            process.output(TerminalUtil.lineSeparator);
            process.output(" Tips:" + TerminalUtil.lineSeparator);
            process.output(tips);
        }
    }

    private static String fillSpace(String src, int size) {

        StringBuilder dist = new StringBuilder(size);

        if (src.length() < size) {
            dist.append(src);

            for (int i = 0; i < size - src.length(); i++) {
                dist.append(' ');
            }

        } else {
            return src;
        }

        return dist.toString();
    }

    public static String readLine(LineReaderImpl reader, String prompt) {
        return reader.readLine(prompt);
    }
    
}
