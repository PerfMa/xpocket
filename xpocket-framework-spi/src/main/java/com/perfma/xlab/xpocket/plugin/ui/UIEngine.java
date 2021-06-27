package com.perfma.xlab.xpocket.plugin.ui;

import com.perfma.xlab.xpocket.plugin.base.NamedObject;
import java.lang.instrument.Instrumentation;

/**
 * @author gongyu <yin.tong@perfma.com>
 */
public interface UIEngine extends NamedObject {

    /**
     * 以某个形式的资源文件启动UI引擎
     *
     * @param def  资源文件形式 例如 META-INF/xpocket.def，xpocket.def作为插件的配置文件，加载插件的时候需要用到
     * @param args
     * 
     * Deprecated since 2.0.1-RELEASE,use start(String[] def, String[] args) instead
     * 
     */
    @Deprecated
    default void start(String def, String[] args) {
        throw new UnsupportedOperationException("It`s default implementation "
                + "for this method,what means your provider never implemented "
                + "this interface. ");
    }
    
    /**
     * start core ui engine with multiple def files
     *
     * @param def  array of the def files,e.g. META-INF/xpocket.def
     * @param args
     * @param inst used when load by a Java Agent
     * 
     */
    default void start(String[] def, String[] args,Instrumentation inst) {
        start(def[0],args);
    }

}
