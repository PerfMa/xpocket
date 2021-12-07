package com.perfma.xlab.xpocket.scroll.utils;

/**
 *
 * @author gongyu <yin.tong@perfma.com>
 */
public class BytesUtils {

    public static boolean isEquals(byte[] src, byte[] target, int offset, int length) {

        boolean result = true;

        offset = offset < 0 ? 0 : offset;
        length = length < 0 ? 0 : length;

        int pos = offset + length;
        if (src.length < pos || target.length < pos) {
            result = false;
        } else {
            for (int i = offset; i < pos; i++) {
                if (src[i] != target[i]) {
                    result = false;
                    break;
                }
            }
        }

        return result;
    }

    public static int paddingSize(int size) {
        int div = size % 8;
        return div == 0 ? 0 : 8 - div;
    }
    
}
