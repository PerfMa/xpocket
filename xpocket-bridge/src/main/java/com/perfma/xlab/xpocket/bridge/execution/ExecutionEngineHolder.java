package com.perfma.xlab.xpocket.bridge.execution;

import com.perfma.xlab.xpocket.plugin.execution.ExecutionEngine;

/**
 * @author xinxian
 * @create 2021-03-24 14:31
 **/
public class ExecutionEngineHolder {

    public static ExecutionEngine getExecutionEgine() {
        return BridgeExecutionEgine.EXECUTION_ENGINE;
    }

    private static class BridgeExecutionEgine {
        private static final ExecutionEngine EXECUTION_ENGINE = new BridgeExecutionExecutionEngineImpl();
    }
}
