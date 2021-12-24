package com.perfma.xlab.xpocket.scroll.groovy;

import com.perfma.xlab.xpocket.scroll.compile.ScrollScriptCompiler;
import java.util.List;
import org.codehaus.groovy.control.CompilerConfiguration;

/**
 *
 * @author gongyu <yin.tong@perfma.com>
 */
public class GroovyScriptCompiler extends GroovyName implements ScrollScriptCompiler {

    public static final String SYS_FUNCS = "import com.perfma.xlab.xpocket.utils.XPocketCommandHelper;void exec(String cmd){XPocketCommandHelper.exec(cmd);};String execByResult(String cmd){return XPocketCommandHelper.execByResult(cmd);};void println(String content){XPocketCommandHelper.println(content);};";
    
    private static final BytesOutputCompiler compiler;
    
    static {
        CompilerConfiguration config = new CompilerConfiguration(CompilerConfiguration.DEFAULT);
        config.getOptimizationOptions().put("asmResolving", Boolean.FALSE);
        config.getOptimizationOptions().put("classLoaderResolving", Boolean.TRUE);

        compiler = new BytesOutputCompiler(config, GroovyScriptCompiler.class.getClassLoader());
    }
    
    
    @Override
    public List<byte[]> compile(String name,String script) {
        script = SYS_FUNCS + script;
        return compiler.compile(name, script);
    }

}
