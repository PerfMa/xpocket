package com.perfma.xlab.xpocket.spi.context;

import com.perfma.xlab.xpocket.spi.command.XPocketCommand;

/**
 * @author gongyu <yin.tong@perfma.com>
 */
public interface CommandBaseInfo {

    /**
     * command name
     *
     * @return
     */
    String name();

    /**
     * command info
     *
     * @return
     */
    String usage();

    /**
     * command index， Determines the order in which information is displayed in Help
     *
     * @return
     */
    int index();

    /**
     * command instance
     *
     * @return
     */
    XPocketCommand instance();
}
