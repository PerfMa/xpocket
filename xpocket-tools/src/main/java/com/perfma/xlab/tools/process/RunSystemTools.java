package com.perfma.xlab.tools.process;

import java.io.IOException;

/**
 * 执行系统命令的工具类
 * @author gongyu
 */
public class RunSystemTools {

    public static Process runCommand(boolean isInheritIO,String... cmds) 
            throws IOException {
        ProcessBuilder builder = createBuilder(isInheritIO,cmds);
        return builder.start();
    }
    
    private static ProcessBuilder createBuilder(boolean isInheritIO,String... cmds) {
        ProcessBuilder builder = new ProcessBuilder(cmds);
        if(isInheritIO) {
            builder.inheritIO();
        }
        return builder;
    }
    
    public static void main(String[] args) {
        
        
        
    }
    
}
