package com.perfma.xlab.xpocket.framework.spi.execution.pipeline;

import com.perfma.xlab.xpocket.plugin.execution.Node;
import java.io.OutputStream;

/**
 *
 * @author gongyu <yin.tong@perfma.com>
 */
public class ExecutionPipeLine {

    protected DefaultProcessDefinition header;

    protected DefaultProcessDefinition tail;

    protected final OutputStream finalOutput;

    protected final Object lock = new Object();

    protected volatile boolean isEnd = false;

    public ExecutionPipeLine(OutputStream finalOutput) {
        this.finalOutput = finalOutput;
    }
    
    public void appendProcess(Node node) {
        DefaultProcessDefinition def = new DefaultProcessDefinition(
                node.getPluginContext(),node.getCmd(),node.getArgs());
        def.setPipeline(this);
        def.setOutputStream(finalOutput);
        
        if(header == null) {
            header = def;
            tail = def;
        } else {
            def.setDefaultEnd(false);
            tail.setOutputStream(new XPocketProcessOutputStream(def));
            tail.setNext(def);
            tail.setPipeline(null);
            tail = def;
        }
    }
    
    public void start() throws Throwable {
        header.execute(null);
        synchronized(lock) {
            if(!isEnd) {
                lock.wait();
            }
        }
    }
    
    public void end() {
        synchronized(lock) {
            isEnd = true;
            lock.notifyAll();
        }
    }
    
    public void interrupt() {
        header.interrupt();
    }
    
    public void userInput(String input) {
        header.userInput(input);
    }
    
}
