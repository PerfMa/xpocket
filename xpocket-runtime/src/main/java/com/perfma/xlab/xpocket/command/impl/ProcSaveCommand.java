package com.perfma.xlab.xpocket.command.impl;

import com.perfma.xlab.xpocket.command.AbstractSystemCommand;
import com.perfma.xlab.xpocket.framework.spi.execution.pipeline.DefaultProcessInfo;
import com.perfma.xlab.xpocket.framework.spi.execution.pipeline.DefaultXPocketProcess;
import com.perfma.xlab.xpocket.framework.spi.impl.CommandProcessor;
import com.perfma.xlab.xpocket.spi.command.CommandInfo;
import com.perfma.xlab.xpocket.spi.process.XPocketProcess;

import java.io.FileWriter;
import java.io.IOException;

/**
 * @author xinxian
 * @create 2020-11-12 20:59
 **/
@CommandInfo(name = "proc_save", usage = "proc_save [-f] [alias] [commandLine]", index = 4)
public class ProcSaveCommand extends AbstractSystemCommand {

    @Override
    public void invoke(XPocketProcess process) {
        try {
            final String[] args = process.getArgs();
            if (args == null || args.length == 0) {
                process.output("command format error: \"process_save [-f] [alias] [commandLine]\"");
                process.end();
                return;
            }
            String alias = null;
            StringBuilder commandLine = new StringBuilder();
            boolean coverFlag = false;
            int coverFlagIndex = -1;
            for (int i = 0; i < args.length; i++) {
                String arg = args[i];
                if ("-f".equals(arg)) {
                    coverFlag = true;
                    coverFlagIndex = i;
                    continue;
                }
                if (i > (coverFlagIndex + 1)) {
                    commandLine.append(" ").append(arg);
                }
            }
            alias = args[coverFlagIndex + 1];
            DefaultProcessInfo defaultProcessInfo = CommandProcessor.buildProcessInfo(commandLine.toString().trim(), (DefaultXPocketProcess) process);
            final boolean exist = CommandProcessor.solutionMap.containsKey(alias);
            if (coverFlag) {
                CommandProcessor.solutionMap.put(alias, defaultProcessInfo);
                write2ProcessFile(alias, commandLine.toString());
            } else {
                CommandProcessor.solutionMap.putIfAbsent(alias, defaultProcessInfo);
                if (!exist) {
                    write2ProcessFile(alias, commandLine.toString());
                }
            }
            if (!exist) {
                CommandProcessor.aliases.add(alias);
            }
            process.output("process save success...");

        } catch (Exception e) {
            process.output(e.getMessage());
        }
        process.end();
    }

    private void write2ProcessFile(String alias, String commandLine) throws IOException {
        try (FileWriter writer = new FileWriter(CommandProcessor.RESOURCE, true)) {
            writer.write(System.getProperty("line.separator"));
            writer.write(alias + "=" + commandLine);
            writer.flush();
        }
    }
}
