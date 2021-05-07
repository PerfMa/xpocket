package com.perfma.xlab.xpocket.utils;

import com.perfma.xlab.xpocket.context.ExecuteContextWrapper;

/**
 * @author: ZQF
 * @date: 2021-04-16
 * @description: desc
 */
public class InternalVariableParse {

    private static final String INTERNAL_VAR_PREFIX = "${";
    private static final String INTERNAL_VAR_SUFFIX = "}";
    private static final String INTERNAL_VAR_SPLIT = "\\.";

    public static String parse(String str, ExecuteContextWrapper executeContextWrapper){
        int index = 0;
        int current = 0;
        StringBuilder builder = new StringBuilder();
        while(index < str.length()) {
            index = str.indexOf(INTERNAL_VAR_PREFIX, index);
            if(index < 0){
                index = str.length();
            }

            int last = str.indexOf(INTERNAL_VAR_SUFFIX, index);
            if(last < 0){
                index = str.length();
            }

            while(current < index){
                builder.append(str.charAt(current ++));
            }

            if(index+INTERNAL_VAR_PREFIX.length() < last){
                String substring = str.substring(index + INTERNAL_VAR_PREFIX.length(), last);
                String[] split = substring.split(INTERNAL_VAR_SPLIT);
                boolean replace = false;
                if(isValid(split)){
                    int row = Integer.parseInt(split[0]);
                    int column = Integer.parseInt(split[1]);
                    String res = executeContextWrapper.get(row, column);
                    if(res != null){
                        replace = true;
                        current = last + 1;
                        builder.append(res);
                    }
                }
                index = last + 1;
                if(!replace){
                    while(current < index){
                        builder.append(str.charAt(current ++));
                    }
                }
            }else if(index < last){
                index = last + 1;
                while(current < index){
                    builder.append(str.charAt(current ++));
                }
            }
        }

        return builder.toString();
    }

    private static boolean isValid(String[] split){
        return 2 == split.length && RegexUtil.isPrimaryKey(split[0]) && RegexUtil.isPrimaryKey(split[1]);
    }

    public static void main(String[] args) {
        ExecuteContextWrapper executeContextWrapper = new ExecuteContextWrapper();
        executeContextWrapper.nextExecuteContext();
        executeContextWrapper.addInternalVar("REPLACE");
        System.out.println(parse("dfsd${1.0}sdfs${2.0},${}", executeContextWrapper));
        System.out.println(executeContextWrapper);
    }

}
