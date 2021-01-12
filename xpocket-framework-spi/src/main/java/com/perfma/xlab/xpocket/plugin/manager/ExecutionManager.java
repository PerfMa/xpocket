package com.perfma.xlab.xpocket.plugin.manager;

import com.perfma.xlab.xpocket.plugin.execution.ExecutionEngine;
import com.perfma.xlab.xpocket.plugin.execution.XpocketProcessInfo;
import org.jline.reader.LineReader;

import java.util.Iterator;
import java.util.ServiceLoader;

/**
 * @author gongyu <yin.tong@perfma.com>
 */
public class ExecutionManager {

    private static ExecutionEngine engine;

    static {
        ServiceLoader<ExecutionEngine> pluginLoaderLoader = ServiceLoader
                .load(ExecutionEngine.class);
        Iterator<ExecutionEngine> it = pluginLoaderLoader.iterator();

        if (it.hasNext()) {
            engine = it.next();
        } else {
            throw new RuntimeException("There is no UI implementation exists in environment!");
        }
    }

    public static void invoke(XpocketProcessInfo process, LineReader reader) throws Throwable {
        engine.invoke(process, reader);
    }

    public static void invoke(XpocketProcessInfo process) throws Throwable {
        engine.invoke(process);
    }

}
