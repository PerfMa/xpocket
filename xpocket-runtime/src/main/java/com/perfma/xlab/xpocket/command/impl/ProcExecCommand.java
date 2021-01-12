package com.perfma.xlab.xpocket.command.impl;

import com.perfma.xlab.xpocket.framework.spi.execution.pipeline.DefaultProcessInfo;
import com.perfma.xlab.xpocket.framework.spi.impl.CommandProcessor;
import com.perfma.xlab.xpocket.plugin.manager.ExecutionManager;
import com.perfma.xlab.xpocket.spi.command.CommandInfo;
import com.perfma.xlab.xpocket.spi.process.XPocketProcess;

/**
 * @author xinxian
 * @create 2020-12-09 16:13
 **/
@CommandInfo(name = "proc_exec", usage = "proc_exec [alias]", index = 3)
public class ProcExecCommand extends AbstractSystemCommand {

    @Override
    public void invoke(XPocketProcess process) throws Throwable {
        final String[] args = process.getArgs();
        if (args == null || args.length != 1) {
            process.output("command format error: \"proc_exec [alias]\"");
            process.end();
            return;
        }

        final DefaultProcessInfo defaultProcessInfo = CommandProcessor.solutionMap.get(args[0]);
        if (defaultProcessInfo == null) {
            process.output("process is empty, you can use the proc_save to add");
            process.end();
            return;
        }
        ExecutionManager.invoke(defaultProcessInfo);
        process.end();
    }
}
