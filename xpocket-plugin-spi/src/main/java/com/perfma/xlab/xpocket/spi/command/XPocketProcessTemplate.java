package com.perfma.xlab.xpocket.spi.command;

import com.perfma.xlab.xpocket.spi.command.callback.ProcessCallback;
import com.perfma.xlab.xpocket.spi.process.XPocketProcess;
import com.perfma.xlab.xpocket.spi.command.callback.SimpleProcessCallback;

import java.util.Arrays;

/**
 * @author gongyu <yin.tong@perfma.com>
 */
public class XPocketProcessTemplate {

    private static final String ERROR_MSG_FORMAT = "ERROR : execute %s failed "
            + "with params %s, on error : %s";

    /**
     * execute process
     *
     * @param process
     * @param callback
     */
    public static void execute(XPocketProcess process,
                               SimpleProcessCallback callback) {
        String cmd = process.getCmd();
        String[] args = process.getArgs();
        try {
            String output = callback.call(cmd, args);
            process.output(output);
        } catch (Throwable ex) {
            process.output(String.format(ERROR_MSG_FORMAT,
                    cmd, Arrays.toString(args), ex.getMessage()));
        } finally {
            process.end();
        }
    }

    public static void execute(XPocketProcess process,
                               ProcessCallback callback) {
        String cmd = process.getCmd();
        String[] args = process.getArgs();
        try {
            String output = callback.call(process, cmd, args);
            process.output(output);
        } catch (Throwable ex) {
            process.output(String.format(ERROR_MSG_FORMAT,
                    cmd, Arrays.toString(args), ex.getMessage()));
        } finally {
            process.end();
        }
    }

}
