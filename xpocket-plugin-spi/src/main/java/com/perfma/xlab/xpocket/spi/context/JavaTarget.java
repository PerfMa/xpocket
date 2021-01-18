package com.perfma.xlab.xpocket.spi.context;

/**
 * @author gongyu <yin.tong@perfma.com>
 */
public enum JavaTarget {
    ALL(0),
    JAVA8(8),
    JAVA9(9),
    JAVA10(10),
    JAVA11(11),
    JAVA12(12),
    JAVA13(13),
    JAVA14(14);

    private int version;

    JavaTarget(int version) {
        this.version = version;
    }

    public int getVersion() {
        return this.version;
    }
}
