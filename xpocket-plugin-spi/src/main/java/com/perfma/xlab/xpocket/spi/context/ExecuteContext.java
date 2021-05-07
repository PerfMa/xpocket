package com.perfma.xlab.xpocket.spi.context;

/**
 * @author: ZQF
 * @date: 2021-04-16
 * @description: desc
 */
public interface ExecuteContext {

    /**
     * 添加内置变量
     * @param var 变量
     */
    void addInternalVar(String var);
}
