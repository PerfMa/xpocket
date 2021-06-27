package com.perfma.xlab.xpocket.bridge.process;

import com.perfma.xlab.xpocket.bridge.XpocketPluginBridge;
import com.perfma.xlab.xpocket.framework.spi.execution.pipeline.DefaultXPocketProcess;
import com.perfma.xlab.xpocket.utils.TerminalUtil;


/**
 * @author xinxian
 * @create 2021-03-23 15:05
 **/
public class BridgeXpocketProcess extends DefaultXPocketProcess {

    public BridgeXpocketProcess(String cmd, String[] args) {
        super(cmd, args);
    }
    @Override
    public void output(String output) {
        try {
            StringBuffer resultCache = XpocketPluginBridge.resultCache;
            if (output != null) {
                String[] lines = output.split(TerminalUtil.lineSeparator);
                for (String line : lines) {
                    if (line != null) {
                        resultCache.append(ansiRemovePattern.matcher(line)
                                .replaceAll(""));
                    }
                }
            }
        } catch (Throwable ex) {
            ex.printStackTrace();
        }
    }
}
