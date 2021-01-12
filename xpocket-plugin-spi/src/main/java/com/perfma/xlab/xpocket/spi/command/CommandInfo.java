package com.perfma.xlab.xpocket.spi.command;

import com.perfma.xlab.xpocket.spi.context.JavaTarget;
import com.perfma.xlab.xpocket.spi.context.PluginType;
import java.lang.annotation.ElementType;
import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 *
 * @author gongyu <yin.tong@perfma.com>
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Repeatable(value=CommandInfos.class)
public @interface CommandInfo {

    String name();
    
    String usage() default "";
    
    PluginType type() default PluginType.ALL;
    
    JavaTarget target() default JavaTarget.ALL;   
    
    int index() default 50;
}
