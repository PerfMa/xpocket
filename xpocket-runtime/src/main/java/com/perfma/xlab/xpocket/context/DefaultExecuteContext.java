package com.perfma.xlab.xpocket.context;

import java.util.ArrayList;
import java.util.List;

/**
 * @author: ZQF
 * @date: 2021-04-16
 * @description: desc
 */
public class DefaultExecuteContext extends AbstractExecuteContext {

    private static final String PLACEHOLDER = "#Placeholder";

    private final List<List<String>> internalVarList = new ArrayList<>();

    private List<String> currentVar;

    DefaultExecuteContext(){
        nextExecuteContext();
    }

    @Override
    public void addInternalVar(String var) {
        assert currentVar != null;
        this.currentVar.add(var);
    }

    @Override
    public void nextExecuteContext() {
        this.currentVar = new ArrayList<>();
        currentVar.add(PLACEHOLDER);
        internalVarList.add(currentVar);
    }

    @Override
    public String get(int row, int column) {
        if(row <= 0 || column < 0){
            return null;
        }
        if(row == internalVarList.size()-1){
            return null;
        }
        if(row < internalVarList.size() && internalVarList.get(row) != null
                && column < internalVarList.get(row).size()){
            return internalVarList.get(row).get(column);
        }
        return null;
    }

    @Override
    public void replaceZero(String value) {
        int second = 2;
        if(internalVarList.size() == second){
            return ;
        }
        this.internalVarList.get(internalVarList.size()-second).set(0, value);
    }

    @Override
    public String toString() {
        return "DefaultExecuteContext{" +
                "internalVarList=" + internalVarList +
                ", currentVar=" + currentVar +
                '}';
    }
}
