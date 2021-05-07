package com.perfma.xlab.xpocket.bridge.process;

import com.perfma.xlab.xpocket.command.impl.SysCommand;
import com.perfma.xlab.xpocket.framework.spi.execution.pipeline.DefaultProcessDefinition;
import com.perfma.xlab.xpocket.framework.spi.impl.XPocketStatusContext;
import com.perfma.xlab.xpocket.spi.command.XPocketCommand;
import com.perfma.xlab.xpocket.spi.context.PluginBaseInfo;

/**
 * @author xinxian
 * @create 2021-03-23 17:02
 **/
public class BridgeProcessDefinition extends DefaultProcessDefinition  {


    public BridgeProcessDefinition(PluginBaseInfo context, String cmd, String[] args) {
        super(context, cmd, args);
    }

    @Override
    public void execute(String input) throws Throwable {
        String[] realArgs;
        if (input == null || input.trim().isEmpty()) {
            realArgs = args;
        } else {
            if (context.getCommand(cmd) != null && context.getCommand(cmd).isPiped()) {
                realArgs = args;
            } else {
                realArgs = new String[args.length + 1];
                realArgs[0] = input;
                System.arraycopy(args, 0, realArgs, 1, args.length);
            }
        }

        BridgeXpocketProcess process = new BridgeXpocketProcess(cmd, realArgs);
        process.setInput(input);
        process.setOutputStream(outputStream);
        process.setPdef(this);
        currentProcess = process;
        hasProcess = true;
        XPocketCommand command = context.getCommand(cmd);
        if (command == null) {
            //未找到的命令尝试交给系统命令
            command = SysCommand.getInstance();
        }
        command.invoke(process, XPocketStatusContext.instance);
    }
}
