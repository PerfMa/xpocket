package com.perfma.xlab.xpocket.ui;

/**
 * @author gongyu <yin.tong@perfma.com>
 */
public interface UIEngine {

    /**
     * 以某个形式的资源文件启动UI引擎
     *
     * @param def  资源文件形式 例如 META-INF/xpocket.def，xpocket.def作为插件的配置文件，加载插件的时候需要用到
     * @param args
     */
    void start(String def, String[] args);

}
