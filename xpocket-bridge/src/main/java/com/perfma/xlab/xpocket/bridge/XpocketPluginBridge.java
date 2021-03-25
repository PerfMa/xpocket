package com.perfma.xlab.xpocket.bridge;

import com.perfma.xlab.xpocket.bridge.dto.Result;
import com.perfma.xlab.xpocket.bridge.execution.ExecutionEngineHolder;
import com.perfma.xlab.xpocket.framework.spi.execution.pipeline.DefaultNode;
import com.perfma.xlab.xpocket.framework.spi.execution.pipeline.DefaultProcessInfo;
import com.perfma.xlab.xpocket.framework.spi.impl.XPocketStatusContext;
import com.perfma.xlab.xpocket.plugin.context.FrameworkPluginContext;
import com.perfma.xlab.xpocket.plugin.manager.CommandManager;
import com.perfma.xlab.xpocket.plugin.manager.PluginManager;
import com.perfma.xlab.xpocket.spi.process.XPocketProcess;
import com.perfma.xlab.xpocket.utils.XPocketConstants;

/**
 * @author xinxian
 * @create 2021-03-23 14:43
 **/
public class XpocketPluginBridge {

    private static DefaultProcessInfo processInfo;

    private static FrameworkPluginContext frameworkPluginContext;

    public static StringBuffer resultCache = new StringBuffer();

    /**
     * 插件间调用，只支持单插件调用，不支持管道符
     * 注意，这里调用包括目标插件的init；switchOn；invoke，但不包括插件的switchOff，switchOff的操作由用户自己调用
     * @param command
     * @param args
     * @param currentXpocketProcess
     */
    public static Result invoke(String command, String[] args, XPocketProcess currentXpocketProcess) {
        //确定目标插件的PluginContext，包装参数信息为DefaultProcessInfo
        FrameworkPluginContext currentFrameworkPluginContext = determineProcessInfo(command, args, currentXpocketProcess);
        if (currentFrameworkPluginContext == null) {
            return Result.buildFail("FrameworkPluginContext Is Empty");
        }
        String errorMsg;
        try {
            if (currentFrameworkPluginContext != frameworkPluginContext) {
                //初始化插件
                frameworkPluginContext.init(currentXpocketProcess);
                //开启插件，资源准备等
                XPocketStatusContext.switchOn(XpocketPluginBridge.frameworkPluginContext,currentXpocketProcess);
                frameworkPluginContext = currentFrameworkPluginContext;
            }
            //命令调用
            ExecutionEngineHolder.getExecutionEgine().invoke(processInfo);
            //构建返回值
            return Result.buildSuccess(resultCache.toString());
        } catch (Throwable throwable) {
            errorMsg = throwable.getMessage();
        } finally {
            //重置返回结果缓存
            resultCache.setLength(0);
        }
        return Result.buildFail(errorMsg);
    }

    /**
     * 插件关闭，这里交给用户自定义调用，因为某些插件的detach等操作放在其中，如果提前关闭，会影响后续调用，为保证功能正常，用户需要自行调用
     * @param currentXpocketProcess
     */
    public static void switchOff(XPocketProcess currentXpocketProcess) {
        XPocketStatusContext.switchOff(frameworkPluginContext,currentXpocketProcess);
    }

    /**
     * 确定目标插件的PluginContext，包装参数信息为DefaultProcessInfo
     * @param command 只支持单插件调用，不支持管道符
     * @param args 参数
     * @param currentXpocketProcess 当前XpocketProcess
     * @return
     */
    private static FrameworkPluginContext determineProcessInfo(String command, String[] args, XPocketProcess currentXpocketProcess) {
        FrameworkPluginContext pluginContext = (FrameworkPluginContext) XPocketStatusContext.instance.currentPlugin();
        if (pluginContext == null) {
            pluginContext = PluginManager.getPlugin(XPocketConstants.SYSTEM_PLUGIN_NAME, XPocketConstants.SYSTEM_PLUGIN_NS);
        }
        DefaultProcessInfo info = new DefaultProcessInfo();
        int index = command.indexOf(".");
        FrameworkPluginContext selected = null;
        String selectedCmd = command;
        if (index > 0) {
            FrameworkPluginContext context = CommandManager.findByCommand(command);
            if (context == null) {
                currentXpocketProcess.output("can not find command :" + command + ".........");
                return null;
            }
            selected = context;
            selectedCmd = command.substring(index + 1);
        } else {
            if (pluginContext.getCommands().contains(command)) {
                selected = pluginContext;
            } else if ((selected = CommandManager.findByCommand(command)) != null) {
                //NOTHING NEED TO DO
            } else {
                selected = PluginManager.getPlugin(XPocketConstants.SYSTEM_PLUGIN_NAME, XPocketConstants.SYSTEM_PLUGIN_NS);
            }
        }
        info.addNode(new DefaultNode(selected, selectedCmd, args));
        processInfo = info;
        return selected;
    }
}
