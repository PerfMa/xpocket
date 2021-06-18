package com.perfma.xlab.xpocket.framework.spi.execution.pipeline;

import com.perfma.xlab.xpocket.command.impl.SysCommand;
import com.perfma.xlab.xpocket.context.ExecuteContextWrapper;
import com.perfma.xlab.xpocket.framework.spi.impl.XPocketStatusContext;
import com.perfma.xlab.xpocket.spi.command.XPocketCommand;
import com.perfma.xlab.xpocket.spi.context.PluginBaseInfo;
import com.perfma.xlab.xpocket.utils.InternalVariableParse;

import java.io.OutputStream;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * @author gongyu <yin.tong@perfma.com>
 */
public class DefaultProcessDefinition {

    protected final PluginBaseInfo context;
    protected final String cmd;
    protected final String[] args;
    protected boolean isEnd = true;

    protected boolean hasProcess = false;
    protected DefaultXPocketProcess currentProcess;

    protected DefaultProcessDefinition next;

    protected OutputStream outputStream;

    protected ExecutionPipeLine pipeline;

    public DefaultProcessDefinition(PluginBaseInfo context, String cmd, String[] args) {
        this.context = context;
        cmd = cmd.replace(context.getName() + ".", "");
        cmd = cmd.replace("@" + context.getNamespace().toUpperCase(), "");
        cmd = cmd.replace("@" + context.getNamespace().toLowerCase(), "");
        this.cmd = cmd;
        this.args = args;
    }

    public void setDefaultEnd(boolean isEnd) {
        this.isEnd = isEnd;
    }

    public void setNext(DefaultProcessDefinition next) {
        this.next = next;
    }

    public void setOutputStream(OutputStream outputStream) {
        this.outputStream = outputStream;
    }

    public void setPipeline(ExecutionPipeLine pipeline) {
        this.pipeline = pipeline;
    }

    public void execute(String input, ExecuteContextWrapper executeContextWrapper) throws Throwable {

        String[] realArgs = this.enableExecuteContext(input, executeContextWrapper, args);
        DefaultXPocketProcess process = new DefaultXPocketProcess(cmd, realArgs);
        process.setInput(input);
        process.setOutputStream(outputStream);
        process.setPdef(this);
        process.setExecuteContext(executeContextWrapper.getExecuteContext());
        currentProcess = process;
        hasProcess = true;
        XPocketCommand command = context.getCommand(cmd);
        if (command == null) {
            //未找到的命令尝试交给系统命令
            command = SysCommand.getInstance();
        }
        command.invoke(process, XPocketStatusContext.instance);
    }

    private String[] enableExecuteContext(String zeroVar, ExecuteContextWrapper executeContextWrapper, String[] args) {
        executeContextWrapper.nextExecuteContext();
        executeContextWrapper.replaceZero(zeroVar);
        
        AtomicBoolean flag = new AtomicBoolean(false);
        String[] res = new String[args == null ? 0 : args.length];
        
        if (args != null && args.length > 0) {
            for (int i = 0; i < args.length; i++) {
                res[i] = InternalVariableParse.parse(args[i], executeContextWrapper, flag);
            }
        }
        
        if (!flag.get()) {
            if (zeroVar != null && !zeroVar.trim().isEmpty()) {
                if (context.getCommand(cmd) == null || !context.getCommand(cmd).isPiped()) {
                    res = new String[args.length + 1];
                    res[0] = zeroVar;
                    System.arraycopy(args, 0, res, 1, args.length);
                }
            }
        }
        return res;
    }

    public void pipeEnd() {
        this.isEnd = true;
        if (!hasProcess) {
            end();
        }
    }

    public void end() {
        hasProcess = false;
        currentProcess = null;
        if (pipeline != null && isEnd) {
            pipeline.end();
        } else if (pipeline == null && isEnd) {
            next.pipeEnd();
        }
    }

    public void interrupt() {
        if (hasProcess) {
            currentProcess.interrupt();
        }

        if (next != null) {
            next.interrupt();
        }
    }

    public void userInput(String input) {
        if (hasProcess) {
            currentProcess.userInput(input);
        }

        if (next != null) {
            next.userInput(input);
        }
    }

}
