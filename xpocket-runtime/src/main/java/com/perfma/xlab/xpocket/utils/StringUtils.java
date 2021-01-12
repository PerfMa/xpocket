package com.perfma.xlab.xpocket.utils;

/**
 *
 * @author gongyu <yin.tong@perfma.com>
 */
public class StringUtils {

    public static boolean isblank(String src) {
        return src == null || src.isEmpty();
    }
    
    public static String fillWithSpace(String src,int length) {
        if(src.length() >= length) {
            return src;
        }
        
        char[] result = new char[length];
        char[] srcArray = src.toCharArray();
        
        System.arraycopy(srcArray, 0, result, 0, srcArray.length);
        
        int pos = src.length();
        while(pos < length) {
            result[pos++] = ' ';
        }
        
        return new String(result);
        
    }
    
}
