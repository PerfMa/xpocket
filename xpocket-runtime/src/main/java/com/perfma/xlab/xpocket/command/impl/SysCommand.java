package com.perfma.xlab.xpocket.command.impl;

import com.perfma.xlab.xpocket.spi.command.CommandInfo;
import com.perfma.xlab.xpocket.spi.process.XPocketProcess;
import com.perfma.xlab.xpocket.utils.TerminalUtil;
import org.apache.commons.exec.*;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;

/**
 * @author xinxian
 * @create 2020-09-28 19:01
 **/
@CommandInfo(name = "ps", usage = "get all process infos", index = 7)
public class SysCommand extends AbstractSystemCommand {
    private static final String DEFAULT_CHARSET = "UTF-8";

    public static volatile SysCommand instance;

    public static SysCommand getInstance() {
        if (instance == null) {
            synchronized (SysCommand.class) {
                if (instance == null) {
                    instance = new SysCommand();
                }
            }
        }
        return instance;
    }

    @Override
    public void invoke(XPocketProcess process) {
        try {
            final String cmd = process.getCmd();
            if (cmd != null && !"".equals(cmd)) {
                final String[] args = process.getArgs();
                if (args != null && args.length > 0) {
                    StringBuilder sb = new StringBuilder(" ");
                    for (int item = 0; item < args.length; item++) {
                        if (item == args.length - 1) {
                            sb.append(args[item]);
                        } else {
                            sb.append(args[item]).append(" ");
                        }
                    }
                    final String result = exeCommand(cmd + sb.toString());
                    process.output(result);
                } else {
                    process.output(exeCommand(cmd));
                }
            }
        } catch (Exception e) {
            process.output("Command not supported..." + TerminalUtil.lineSeparator);
        }
        process.end();
    }

    /**
     * 执行指定命令
     *
     * @param command 命令
     * @return 命令执行完成返回结果
     * @throws IOException 失败时抛出异常，由调用者捕获处理
     */
    private String exeCommand(String command) throws IOException {
        try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            int exitCode = exeCommand(command, out);
            if (exitCode != 0) {
                throw new RuntimeException("命令运行失败!");
            }
            return out.toString(DEFAULT_CHARSET);
        }
    }

    /**
     * 执行指定命令，输出结果到指定输出流中
     *
     * @param command 命令
     * @param out     执行结果输出流
     * @return 执行结果状态码：执行成功返回0
     * @throws ExecuteException 失败时抛出异常，由调用者捕获处理
     * @throws IOException      失败时抛出异常，由调用者捕获处理
     */
    public int exeCommand(String command, OutputStream out) throws ExecuteException, IOException {
        CommandLine commandLine = CommandLine.parse(command);
        PumpStreamHandler pumpStreamHandler = null;
        if (null == out) {
            pumpStreamHandler = new PumpStreamHandler();
        } else {
            pumpStreamHandler = new PumpStreamHandler(out);
        }
        // 设置超时时间为10秒
        ExecuteWatchdog watchdog = new ExecuteWatchdog(10000);
        DefaultExecutor executor = new DefaultExecutor();
        executor.setStreamHandler(pumpStreamHandler);
        executor.setWatchdog(watchdog);
        return executor.execute(commandLine, System.getenv());
    }
}
