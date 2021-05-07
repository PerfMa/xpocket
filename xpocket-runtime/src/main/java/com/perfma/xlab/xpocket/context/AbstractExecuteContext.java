package com.perfma.xlab.xpocket.context;

import com.perfma.xlab.xpocket.spi.context.ExecuteContext;

/**
 * @author: ZQF
 * @date: 2021-04-16
 * @description: desc
 */
public abstract class AbstractExecuteContext implements ExecuteContext {
    public abstract void nextExecuteContext();

    public abstract String get(int row, int column);

    public abstract void replaceZero(String value);
}
