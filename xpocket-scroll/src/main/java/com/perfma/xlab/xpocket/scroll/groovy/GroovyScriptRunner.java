package com.perfma.xlab.xpocket.scroll.groovy;

import com.perfma.xlab.xpocket.scroll.run.ScrollScriptRunner;
import groovy.lang.GroovyShell;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.net.URL;
import java.util.List;
import org.codehaus.groovy.control.CompilerConfiguration;

/**
 *
 * @author gongyu <yin.tong@perfma.com>
 */
public class GroovyScriptRunner extends GroovyName implements ScrollScriptRunner {

    private static final GroovyShell shell;

    private static final DynamicClassLoader loader = new DynamicClassLoader(new URL[0], GroovyScriptRunner.class.getClassLoader());

    static {
        CompilerConfiguration config = new CompilerConfiguration(CompilerConfiguration.DEFAULT);
        config.getOptimizationOptions().put("asmResolving", Boolean.FALSE);
        config.getOptimizationOptions().put("classLoaderResolving", Boolean.TRUE);
        shell = new GroovyShell(GroovyScriptRunner.class.getClassLoader(), config);
    }

    @Override
    public void run(String script) {
        shell.evaluate(script);
    }

    @Override
    public void run(List<byte[]> script) {
        for (byte[] b : script) {
            try {
                Class clazz = loader.loadClass(b, 0, b.length);
                Method m = clazz.getMethod("main", String[].class);
                if(m != null && Modifier.isStatic(m.getModifiers())) {
                    m.invoke(null, (Object) new String[0]);
                }
            } catch (Throwable ex) {
                throw new RuntimeException("Error in groovy script running.", ex);
            }
        }
    }

}
