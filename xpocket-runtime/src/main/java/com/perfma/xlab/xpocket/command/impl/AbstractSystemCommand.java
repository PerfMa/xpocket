package com.perfma.xlab.xpocket.command.impl;

import com.perfma.xlab.xpocket.spi.command.AbstractXPocketCommand;
import org.jline.reader.impl.LineReaderImpl;

/**
 * @author gongyu <yin.tong@perfma.com>
 */
public abstract class AbstractSystemCommand extends AbstractXPocketCommand {

    private LineReaderImpl reader;

    protected LineReaderImpl getReader() {
        return reader;
    }

    public void setReader(LineReaderImpl reader) {
        this.reader = reader;
    }
}
