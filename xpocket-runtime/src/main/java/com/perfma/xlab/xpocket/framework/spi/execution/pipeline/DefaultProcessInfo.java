package com.perfma.xlab.xpocket.framework.spi.execution.pipeline;

import com.perfma.xlab.xpocket.plugin.execution.Node;
import com.perfma.xlab.xpocket.plugin.execution.XpocketProcessInfo;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author gongyu <yin.tong@perfma.com>
 */
public class DefaultProcessInfo implements XpocketProcessInfo {

    private final List<Node> nodes = new ArrayList<>();
    
    private OutputStream output;
    
    @Override
    public List<Node> nodes() {
        return nodes;
    }

    @Override
    public OutputStream finalOutput() {
        return output;
    }

    public void setOutput(OutputStream output) {
        this.output = output;
    }
    
    public void addNode(Node node) {
        this.nodes.add(node);
    }
  
}
