package com.perfma.xlab.xpocket.context;


/**
 * @author: ZQF
 * @date: 2021-04-16
 * @description: desc
 */
public class ExecuteContextWrapper {

    private AbstractExecuteContext executeContext;

    public ExecuteContextWrapper(){
        this.executeContext = new DefaultExecuteContext();
    }

    public void nextExecuteContext(){
        this.executeContext.nextExecuteContext();
    }

    public String get(int row, int column){
        return this.executeContext.get(row, column);
    }

    public void addInternalVar(String var){
        this.executeContext.addInternalVar(var);
    }

    public void replaceZero(String var){
        this.executeContext.replaceZero(var);
    }

    public AbstractExecuteContext getExecuteContext() {
        return executeContext;
    }

    @Override
    public String toString() {
        return "ExecuteContextWrapper{" +
                "executeContext=" + executeContext +
                '}';
    }
}
