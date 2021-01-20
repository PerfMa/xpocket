package com.perfma.xlab.xpocket.plugin.execution;

import com.perfma.xlab.xpocket.plugin.base.NamedObject;
import org.jline.reader.LineReader;

/**
 * @author gongyu <yin.tong@perfma.com>
 */
public interface ExecutionEngine extends NamedObject {

    /**
     * An instruction to
     *
     * @param process Encapsulates instruction information
     * @param reader  Command line information reading class, assist the completion of terminal control
     * @throws Throwable
     */
    void invoke(XpocketProcessInfo process, LineReader reader) throws Throwable;


    void invoke(XpocketProcessInfo info) throws Throwable;

}
