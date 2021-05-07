package com.perfma.xlab.xpocket.framework.spi.execution.pipeline;

import com.perfma.xlab.xpocket.context.ExecuteContextWrapper;

import java.io.IOException;
import java.io.OutputStream;

/**
 *
 * @author gongyu <yin.tong@perfma.com>
 */
public class XPocketProcessOutputStream extends OutputStream {

    private final DefaultProcessDefinition def;
    private final ExecuteContextWrapper executeContextWrapper;

    public XPocketProcessOutputStream(DefaultProcessDefinition def, ExecuteContextWrapper executeContextWrapper) {
        this.def = def;
        this.executeContextWrapper = executeContextWrapper;
    }
    
    @Override
    public void write(int b) throws IOException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void write(byte[] b, int off, int len) throws IOException {
        byte[] readyToWrite =  new byte[len];
        System.arraycopy(b, off, readyToWrite, 0, len);
        try {
            def.execute(new String(readyToWrite), executeContextWrapper);
        } catch (Throwable ex) {
            def.end();
            throw new IOException(ex);
        }
    }
    
    

}
