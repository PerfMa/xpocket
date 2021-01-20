package com.perfma.xlab.xpocket.plugin.manager;

import com.perfma.xlab.xpocket.plugin.execution.ExecutionEngine;
import com.perfma.xlab.xpocket.plugin.execution.XpocketProcessInfo;
import com.perfma.xlab.xpocket.plugin.util.Constants;
import com.perfma.xlab.xpocket.plugin.util.ServiceLoaderUtils;
import org.jline.reader.LineReader;

import java.util.Map;

/**
 * @author gongyu <yin.tong@perfma.com>
 */
public class ExecutionManager {

    private static final ExecutionEngine engine;

    static {
        Map<String,ExecutionEngine> engines = ServiceLoaderUtils.loadServices(ExecutionEngine.class);
        String run_mode = System.getProperty(Constants.RUN_MODE_KEY, Constants.DEFAULT_RUN_MODE).toUpperCase();

        if (engines.containsKey(run_mode)) {
            engine = engines.get(run_mode);
        } else {
            engine = engines.get(Constants.DEFAULT_RUN_MODE);
        }
        
        if(engine == null) {
            throw new RuntimeException("There is no ENGINE implementation exists in environment!");
        }
    }

    public static void invoke(XpocketProcessInfo process, LineReader reader) throws Throwable {
        engine.invoke(process, reader);
    }

    public static void invoke(XpocketProcessInfo process) throws Throwable {
        engine.invoke(process);
    }

}
