package com.perfma.xlab.xpocket.framework.spi.impl;

import com.perfma.xlab.xpocket.framework.spi.execution.pipeline.ExecutionPipeLine;
import com.perfma.xlab.xpocket.plugin.execution.ExecutionEngine;
import com.perfma.xlab.xpocket.plugin.execution.Node;
import com.perfma.xlab.xpocket.plugin.execution.XpocketProcessInfo;
import org.jline.reader.LineReader;
import org.jline.reader.UserInterruptException;

import java.util.concurrent.atomic.AtomicBoolean;

/**
 * @author gongyu <yin.tong@perfma.com>
 */
public class DefaultExecutionEngine extends DefaultNamedObject implements ExecutionEngine {

    @Override
    public void invoke(XpocketProcessInfo info, LineReader reader) throws Throwable {

        if(reader == null) {
            invoke(info);
            return;
        }
        
        ExecutionPipeLine pipeline = new ExecutionPipeLine(info.finalOutput());

        for (Node node : info.nodes()) {
            pipeline.appendProcess(node);
        }

        final AtomicBoolean isEnd = new AtomicBoolean(false);
        Thread mainThread = Thread.currentThread();
        Throwable[] exs = new Throwable[1];

        Thread executeThread = new Thread(() -> {
            try {
                pipeline.start();
            } catch (InterruptedException ie) {
                //ignore it
                isEnd.set(true);
                return;
            } catch (Throwable ex) {
                if (!isEnd.get()) {
                    exs[0] = ex;
                }
            }
            if (isEnd.compareAndSet(false, true)) {
                mainThread.interrupt();
            }
        });
        executeThread.start();

        try {
            for (; ; ) {
                if (isEnd.get()) {
                    break;
                }
                final String input = reader.readLine();
                pipeline.userInput(input);
            }
        } catch (UserInterruptException ex) {
            executeThread.interrupt();
            pipeline.interrupt();
            isEnd.set(true);
            //ignore
        }

        if (exs[0] != null) {
            throw exs[0];
        }
    }

    @Override
    public void invoke(XpocketProcessInfo info) throws Throwable {

        ExecutionPipeLine pipeline = new ExecutionPipeLine(info.finalOutput());

        for (Node node : info.nodes()) {
            pipeline.appendProcess(node);
        }

        pipeline.start();
    }

}
