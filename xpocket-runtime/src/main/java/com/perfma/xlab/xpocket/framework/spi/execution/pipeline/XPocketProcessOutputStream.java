package com.perfma.xlab.xpocket.framework.spi.execution.pipeline;

import java.io.IOException;
import java.io.OutputStream;

/**
 * @author gongyu <yin.tong@perfma.com>
 */
public class XPocketProcessOutputStream extends OutputStream {

    private final DefaultProcessDefinition def;

    public XPocketProcessOutputStream(DefaultProcessDefinition def) {
        this.def = def;
    }

    @Override
    public void write(int b) throws IOException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void write(byte[] b, int off, int len) throws IOException {
        byte[] readyToWrite = new byte[len];
        System.arraycopy(b, off, readyToWrite, 0, len);
        try {
            def.execute(new String(readyToWrite));
        } catch (Throwable ex) {
            def.end();
            throw new IOException(ex);
        }
    }


}
