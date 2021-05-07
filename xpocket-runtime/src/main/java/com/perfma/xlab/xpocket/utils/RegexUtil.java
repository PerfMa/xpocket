package com.perfma.xlab.xpocket.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author: ZQF
 * @date: 2021-04-16
 * @description: desc
 */
public class RegexUtil {
    private static final Pattern PRIMARY_KEY_PATTERN = Pattern.compile("^\\d+$");

    public static boolean isPrimaryKey(String value){
        if(value == null){
            return false;
        }
        Matcher matcher = PRIMARY_KEY_PATTERN.matcher(value);
        return matcher.find();
    }
}
