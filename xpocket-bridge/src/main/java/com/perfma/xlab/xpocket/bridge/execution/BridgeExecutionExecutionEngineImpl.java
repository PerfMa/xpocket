package com.perfma.xlab.xpocket.bridge.execution;

import com.perfma.xlab.xpocket.plugin.execution.ExecutionEngine;
import com.perfma.xlab.xpocket.plugin.execution.Node;
import com.perfma.xlab.xpocket.plugin.execution.XpocketProcessInfo;
import org.jline.reader.LineReader;

/**
 * @author xinxian
 * @create 2021-03-23 16:42
 **/
public class BridgeExecutionExecutionEngineImpl extends BridgeNamedObject implements ExecutionEngine {

    @Override
    public void invoke(XpocketProcessInfo process, LineReader reader) throws Throwable {
        //NOTHING TO DO
    }

    @Override
    public void invoke(XpocketProcessInfo info) throws Throwable {
        BridgeExecutionPipeLine pipeline = new BridgeExecutionPipeLine(info.finalOutput());
        //其实插件之间调用只支持单插件，所以这里的nodes只有一个元素
        for (Node node : info.nodes()) {
            pipeline.appendProcess(node);
        }
        pipeline.start();
    }
}
