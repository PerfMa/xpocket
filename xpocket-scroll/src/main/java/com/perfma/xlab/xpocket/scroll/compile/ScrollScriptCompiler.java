package com.perfma.xlab.xpocket.scroll.compile;

import com.perfma.xlab.xpocket.scroll.ScriptName;
import java.util.List;

/**
 *
 * @author gongyu <yin.tong@perfma.com>
 */
public interface ScrollScriptCompiler extends ScriptName {
    
    /**
     * compile input script to byte array
     * @param name
     * @param script script content
     * @return byte array of compiled script.
     */
    List<byte[]> compile(String name,String script);
    
}
