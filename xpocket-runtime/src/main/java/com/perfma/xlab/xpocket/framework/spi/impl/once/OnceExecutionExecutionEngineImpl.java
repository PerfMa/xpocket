package com.perfma.xlab.xpocket.framework.spi.impl.once;

import com.perfma.xlab.xpocket.console.EndOfInputException;
import com.perfma.xlab.xpocket.framework.spi.execution.pipeline.ExecutionPipeLine;
import com.perfma.xlab.xpocket.plugin.execution.ExecutionEngine;
import com.perfma.xlab.xpocket.plugin.execution.Node;
import com.perfma.xlab.xpocket.plugin.execution.XpocketProcessInfo;
import org.jline.reader.LineReader;


/**
 * @author gongyu <yin.tong@perfma.com>
 */
public class OnceExecutionExecutionEngineImpl extends OnceNamedObject implements ExecutionEngine {
    
    @Override
    public void invoke(XpocketProcessInfo info, LineReader reader) throws Throwable {

        ExecutionPipeLine pipeline = new ExecutionPipeLine(info.finalOutput());

        for (Node node : info.nodes()) {
            pipeline.appendProcess(node);
        }

        Thread mainThread = Thread.currentThread();
        Throwable[] exs = new Throwable[1];

        Thread executeThread = new Thread(() -> {
            try {
                pipeline.start();
            } catch (InterruptedException | EndOfInputException ie) {
                return;
            } catch (Throwable ex) {
                exs[0] = ex;
            }
        });
        executeThread.start();

        executeThread.join();

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
