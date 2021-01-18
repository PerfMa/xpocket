package com.perfma.xlab.xpocket.framework.spi.execution.pipeline;

import com.perfma.xlab.xpocket.console.Output;
import com.perfma.xlab.xpocket.spi.process.XPocketProcess;
import com.perfma.xlab.xpocket.spi.process.XPocketProcessAction;
import com.perfma.xlab.xpocket.utils.TerminalUtil;

import java.io.OutputStream;
import java.io.PrintStream;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Pattern;

/**
 * @author gongyu <yin.tong@perfma.com>
 */
public class DefaultXPocketProcess implements XPocketProcess {

    private final Pattern ansiRemovePattern = Pattern.compile("(@\\|\\w* )|( ?\\|@)");
    private final String cmd;
    private final String[] args;
    private final Set<XPocketProcessAction> actions = new HashSet<>();
    private String input;
    private OutputStream outputStream;
    private DefaultProcessDefinition pdef;
    private Output output;

    public DefaultXPocketProcess(String cmd, String[] args) {
        this.cmd = cmd;
        this.args = args;
    }

    @Override
    public String getCmd() {
        return cmd;
    }

    @Override
    public String[] getArgs() {
        return args;
    }

    @Override
    public String input() {
        return input;
    }

    @Override
    public void output(String output) {
        try {
            if (output != null) {
                if (this.output != null) {
                    String[] lines = output.split(TerminalUtil.lineSeparator);
                    if (lines.length == 0) {
                        this.output.print(output);
                    } else {
                        for (String line : lines) {
                            this.output.print(simpleFormatOutput(line));
                        }
                    }
                    outputStream.flush();
                } else {
                    String[] lines = output.split(TerminalUtil.lineSeparator);
                    for (String line : lines) {
                        if (line != null) {
                            outputStream.write(ansiRemovePattern.matcher(line)
                                    .replaceAll("").getBytes());
                        }
                    }
                }
            }
        } catch (Throwable ex) {
            ex.printStackTrace();
        }
    }

    private String simpleFormatOutput(String output) {
        String result = output;

        if (!result.startsWith(" ")) {
            result = " " + result;
        }

        if (!result.endsWith(TerminalUtil.lineSeparator)) {
            result += TerminalUtil.lineSeparator;
        }

        return result;
    }

    @Override
    public void end() {
        if (pdef != null) {
            pdef.end();
        }
    }

    public void setInput(String input) {
        this.input = input;
    }

    public void setOutputStream(OutputStream outputStream) {
        this.outputStream = outputStream;
        if (outputStream instanceof PrintStream) {
            output = new Output((PrintStream) outputStream, true);
        }
    }

    public OutputStream getOutputStream() {
        return outputStream;
    }

    public void setPdef(DefaultProcessDefinition pdef) {
        this.pdef = pdef;
    }

    @Override
    public void register(XPocketProcessAction action) {
        actions.add(action);
    }

    @Override
    public void unregister(XPocketProcessAction action) {
        actions.remove(action);
    }

    public void interrupt() {
        for (XPocketProcessAction action : actions) {
            try {
                action.interrupt();
            } catch (Throwable ex) {
                //ignore
            }
        }
    }

    public void userInput(String userInput) {
        for (XPocketProcessAction action : actions) {
            try {
                action.userInput(userInput);
            } catch (Throwable ex) {
                //ignore
            }
        }
    }

}
