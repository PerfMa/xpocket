package com.perfma.xlab.xpocket.scroll.run;

import com.perfma.xlab.xpocket.scroll.ScriptName;
import java.util.List;

/**
 *
 * @author gongyu <yin.tong@perfma.com>
 */
public interface ScrollScriptRunner extends ScriptName {

    /**
     * run string script.
     * @param script 
     */
    void run(String script);
    
    /**
     * run script with compiled stream
     * @param script 
     */
    void run(List<byte[]> script);
    
}
