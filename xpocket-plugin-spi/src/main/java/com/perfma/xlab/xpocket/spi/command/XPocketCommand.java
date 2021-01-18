package com.perfma.xlab.xpocket.spi.command;

import com.perfma.xlab.xpocket.spi.XPocketPlugin;
import com.perfma.xlab.xpocket.spi.context.SessionContext;
import com.perfma.xlab.xpocket.spi.process.XPocketProcess;

/**
 * @author gongyu <yin.tong@perfma.com>
 */
public interface XPocketCommand {


    /**
     * init XPocketCommand instance
     *
     * @param plugin
     */
    void init(XPocketPlugin plugin);

    /**
     * Do this command support piped execution.
     *
     * @return
     */
    boolean isPiped();

    /**
     * checking the command is avaible now
     *
     * @param cmd
     * @return
     */
    boolean isAvailableNow(String cmd);

    /**
     * detail current command
     *
     * @return
     */
    String details(String cmd);

    /**
     * invoke
     *
     * @param process
     * @param context
     * @throws java.lang.Throwable
     */
    void invoke(XPocketProcess process, SessionContext context) throws Throwable;

    /**
     * return some details of command usage.
     *
     * @return
     */
    String[] tips();

}
