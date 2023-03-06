package com.perfma.xlab.xpocket.scroll.groovy;

import groovy.lang.GroovyClassLoader;
import java.lang.reflect.Field;
import java.util.Deque;
import org.codehaus.groovy.control.CompilationUnit;
import org.codehaus.groovy.control.CompilerConfiguration;
import org.codehaus.groovy.control.Phases;
import sun.misc.Unsafe;



public class BytesOutputCompilationUnit extends CompilationUnit {

    private static Unsafe theUnsafe;
    private static long PHASE_OPERATIONS_ADDRESS;
    
    static {
        try {
            Field f = Unsafe.class.getDeclaredField("theUnsafe");
            f.setAccessible(true);
            theUnsafe = (Unsafe)f.get(null);
            f.setAccessible(false);
            PHASE_OPERATIONS_ADDRESS = theUnsafe.objectFieldOffset(
                    CompilationUnit.class.getDeclaredField("phaseOperations"));
        } catch (Throwable ex) {
            ex.printStackTrace();
        }   
    }
    
    public BytesOutputCompilationUnit(CompilerConfiguration configuration, GroovyClassLoader loader) {
        super(configuration, null, loader, null);
        Deque[] OPS = (Deque[])theUnsafe.getObject(this, PHASE_OPERATIONS_ADDRESS);
        OPS[Phases.OUTPUT].clear();
    }
}
