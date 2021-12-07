package com.perfma.xlab.xpocket.scroll.groovy;

import groovy.lang.GroovyClassLoader;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import org.codehaus.groovy.control.CompilationFailedException;
import org.codehaus.groovy.control.CompilationUnit;
import org.codehaus.groovy.control.CompilerConfiguration;
import org.codehaus.groovy.control.SourceUnit;
import org.codehaus.groovy.control.messages.Message;

/**
 *
 * @author gongyu <yin.tong@perfma.com>
 */
public class BytesOutputCompiler {

    private final CompilerConfiguration configuration;  // Optional configuration data
    
    private final GroovyClassLoader loader;

    /**
     * Initializes the Compiler with default configuration.
     */
    public BytesOutputCompiler() {
        this(null,null);
    }


    /**
     * Initializes the Compiler with the specified configuration.
     * @param configuration
     */
    public BytesOutputCompiler(CompilerConfiguration configuration) {
        this(configuration,null);
    }
    
    /**
     * Initializes the Compiler with the specified configuration.
     * @param configuration
     * @param loader
     */
    public BytesOutputCompiler(CompilerConfiguration configuration,ClassLoader loader) {
        this.configuration = configuration;
        this.loader = new GroovyClassLoader(loader);
    }
    
        /**
     * Compiles a string of code.
     * @param name
     * @param code
     * @return byte array of compiled code
     */
    public List<byte[]> compile(String name, String code) throws CompilationFailedException {
        List<byte[]> result = new ArrayList<>();
        BytesOutputCompilationUnit unit = new BytesOutputCompilationUnit(configuration,loader);
        unit.addPhaseOperation(groovyClass -> {
            // create the file and write out the data
            try {
                result.add(groovyClass.getBytes());
            } catch (Throwable e) {
                unit.getErrorCollector().addError(Message.create(e.getMessage(), unit));
            }
        });
        unit.addSource(new SourceUnit(name, code, configuration, unit.getClassLoader(), unit.getErrorCollector()));
        unit.compile();
        return result;
    }
    
}