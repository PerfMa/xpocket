package com.perfma.xlab.xpocket.bridge.execution;

import com.perfma.xlab.xpocket.bridge.process.BridgeProcessDefinition;
import com.perfma.xlab.xpocket.framework.spi.execution.pipeline.ExecutionPipeLine;
import com.perfma.xlab.xpocket.framework.spi.execution.pipeline.XPocketProcessOutputStream;
import com.perfma.xlab.xpocket.plugin.execution.Node;

import java.io.OutputStream;

/**
 * @author xinxian
 * @create 2021-03-23 17:30
 **/
public class BridgeExecutionPipeLine extends ExecutionPipeLine {
    public BridgeExecutionPipeLine(OutputStream finalOutput) {
        super(finalOutput);
    }

    @Override
    public void appendProcess(Node node) {
        BridgeProcessDefinition def = new BridgeProcessDefinition(
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
}
