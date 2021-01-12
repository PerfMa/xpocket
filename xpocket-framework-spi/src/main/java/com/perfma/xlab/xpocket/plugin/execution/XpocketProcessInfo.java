package com.perfma.xlab.xpocket.plugin.execution;

import java.io.OutputStream;
import java.util.List;

/**
 * @author gongyu <yin.tong@perfma.com>
 */
public interface XpocketProcessInfo {

    /**
     * Gets a list of command information
     *
     * @return
     */
    List<Node> nodes();

    /**
     * Get the output stream
     *
     * @return
     */
    OutputStream finalOutput();

}
