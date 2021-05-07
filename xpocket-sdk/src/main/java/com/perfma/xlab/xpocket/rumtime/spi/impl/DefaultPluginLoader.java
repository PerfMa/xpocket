package com.perfma.xlab.xpocket.rumtime.spi.impl;

import com.perfma.xlab.xpocket.classloader.XPocketPluginClassLoader;
import com.perfma.xlab.xpocket.framework.spi.impl.DefaultCommandContext;
import com.perfma.xlab.xpocket.framework.spi.impl.DefaultPluginContext;
import com.perfma.xlab.xpocket.plugin.context.FrameworkPluginContext;
import com.perfma.xlab.xpocket.plugin.loader.PluginLoader;
import com.perfma.xlab.xpocket.spi.XPocketPlugin;
import com.perfma.xlab.xpocket.spi.command.CommandInfo;
import com.perfma.xlab.xpocket.spi.command.CommandList;
import com.perfma.xlab.xpocket.spi.command.XPocketCommand;
import com.perfma.xlab.xpocket.utils.StringUtils;

import java.io.File;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.*;

/**
 * @author xinxian
 * @create 2021-01-19 17:30
 **/
public class DefaultPluginLoader implements PluginLoader {

    private static final String XLAB_SPI_PACKAGE = "com.perfma.xlab.xpocket.spi.";

    private static final Class XPOCKET_COMMAND_CLASS = XPocketCommand.class;

    private static final Class XPOCKET_PLUGIN_CLASS = XPocketPlugin.class;

    private static final String PLUGIN_UNI_FLAG_FORMAT = "%s@%s";

    private static String lineSeparator = System.getProperty("line.separator");

    private final HashMap<String, FrameworkPluginContext> pluginMap = new HashMap<>();

    @Override
    public boolean loadPlugins(String resouceName) {
        try {
            HashSet<String> nameUniIndex = new HashSet<>();
            XPocketPluginClassLoader pluginLoader
                    = new XPocketPluginClassLoader(
                    new URL[]{new File(System.getProperty("user.dir")).toURI().toURL()},
                    DefaultPluginLoader.class.getClassLoader());

            URL pluginDef = Thread.currentThread().getContextClassLoader().getResource(resouceName);
            try (InputStreamReader reader
                         = new InputStreamReader(pluginDef.openStream(),
                    Charset.forName("UTF-8"))) {
                Properties prop = new Properties();
                prop.load(reader);
                if (!checkProperties(prop)) {
                    return false;
                }
                DefaultPluginContext context = new DefaultPluginContext();
                context.setName(prop.getProperty("plugin-name"));
                context.setNamespace(prop.getProperty("plugin-namespace"));
                String pluginMain = prop.getProperty("main-implementation");
                String desc = prop.getProperty("plugin-description");
                String tips = prop.getProperty("usage-tips");
                String plugin_author = prop.getProperty("plugin-author");
                String plugin_project = prop.getProperty("plugin-project");
                String tools_author = prop.getProperty("tools-author");
                String tools_project = prop.getProperty("tools-project");

                if (desc != null && !desc.isEmpty()) {
                    context.setDescription(desc);
                }

                if (tips != null && !tips.isEmpty()) {
                    context.setTips(tips);
                }

                String pluginInfo = "";

                if (!StringUtils.isblank(plugin_author)) {
                    pluginInfo += "@|white plugin-author       : " + plugin_author + " |@" + lineSeparator;
                }

                if (!StringUtils.isblank(plugin_project)) {
                    pluginInfo += "@|white plugin-project      : " + plugin_project + " |@" + lineSeparator;
                }

                if (!StringUtils.isblank(tools_author)) {
                    pluginInfo += "@|white tool-author         : " + tools_author + " |@" + lineSeparator;
                }

                if (!StringUtils.isblank(tools_project)) {
                    pluginInfo += "@|white tool-project        : " + tools_project + " |@" + lineSeparator;
                }

                context.setPluginInfo(pluginInfo);

                if (pluginMain != null) {
                    context.setPluginClass(pluginLoader.loadClass(pluginMain));
                }
                String commandPackage = prop.getProperty("plugin-command-package");
                //scan classes
                List<String> classNames = new ArrayList<>();
                final String target = pluginDef.getPath().substring(0, pluginDef.getPath().indexOf("target"));
                listClassNames(classNames, new File(target + "target"), commandPackage);
                //use XPocketPluginClassLoader
                ClassLoader currentTCL = Thread.currentThread()
                        .getContextClassLoader();
                HashMap<String, DefaultCommandContext> cmdMap = new HashMap<>();
                try {
                    try {
                        Thread.currentThread().setContextClassLoader(pluginLoader);
                        for (String pluginClassName : classNames) {
                            Class pluginClass = pluginLoader.loadClass(pluginClassName);
                            if (XPOCKET_COMMAND_CLASS.isAssignableFrom(pluginClass)) {
                                XPocketCommand commandObject
                                        = (XPocketCommand) pluginClass.getConstructor()
                                        .newInstance();

                                //collect commandinfo information
                                CommandInfo[] infos
                                        = (CommandInfo[]) pluginClass
                                        .getAnnotationsByType(
                                                CommandInfo.class);
                                for (CommandInfo info : infos) {
                                    cmdMap.put(info.name(),
                                            new DefaultCommandContext(info.name(),
                                                    info.usage(), 1, commandObject));
                                }

                                //collect commandlist infomation
                                CommandList[] lists
                                        = (CommandList[]) pluginClass
                                        .getAnnotationsByType(
                                                CommandList.class);
                                for (CommandList list : lists) {
                                    String[] names = list.names();
                                    String[] usages = list.usage();

                                    for (int i = 0; i < names.length; i++) {
                                        cmdMap.put(names[i],
                                                new DefaultCommandContext(names[i],
                                                        usages.length > i
                                                                ? usages[i]
                                                                : "", 50, commandObject));
                                    }
                                }
                            } else if (XPOCKET_PLUGIN_CLASS.isAssignableFrom(pluginClass)
                                    && pluginMain == null) {
                                context.setPluginClass(pluginClass);
                            }
                        }
                    } finally {
                        Thread.currentThread().setContextClassLoader(currentTCL);
                    }

                    context.setCommands(cmdMap);

                    if (nameUniIndex.contains(context.getName())) {
                        pluginMap.remove(context.getName());
                    } else {
                        pluginMap.put(context.getName(), context);
                        nameUniIndex.add(context.getName());
                    }

                    pluginMap.put(String.format(PLUGIN_UNI_FLAG_FORMAT,
                            context.getName(), context.getNamespace().toUpperCase()),
                            context);
                } catch (Throwable ex) {
                    System.out.println(String.format("load plugin %s error : \n %s",
                            context.getName(), ex));
                    return false;
                }
            }
        } catch (Throwable ex) {
            ex.printStackTrace();
            return false;
        }

        return true;
    }

    @Override
    public Set<FrameworkPluginContext> getAvailablePlugins() {
        return new HashSet<>(pluginMap.values());
    }

    @Override
    public Set<FrameworkPluginContext> getAllPlugins() {
        return new HashSet<>(pluginMap.values());
    }

    @Override
    public void addPlugin(FrameworkPluginContext pluginContext) {
        if (!pluginMap.containsKey(pluginContext.getName())) {
            pluginMap.put(pluginContext.getName(), pluginContext);
        }
        pluginMap.put(String.format(PLUGIN_UNI_FLAG_FORMAT, pluginContext.getName(),
                pluginContext.getNamespace().toUpperCase()), pluginContext);
    }

    @Override
    public FrameworkPluginContext getPlugin(String name, String namespace) {
        if (namespace == null) {
            return pluginMap.get(name);
        }
        return pluginMap.get(String.format(PLUGIN_UNI_FLAG_FORMAT, name,
                namespace.toUpperCase()));
    }

    private static void listClassNames(List<String> classNames, File file, String commandPackage) {
        final File[] files = file.listFiles();
        if (files != null) {
            for (File item : files) {
                if (!item.isDirectory()) {
                    if (item.getPath().endsWith(".class") && item.getPath().replace('/', '.').contains(commandPackage)) {
                        String className = item.getPath().replace('/', '.')
                                .substring(item.getPath().indexOf("com"), item.getPath().lastIndexOf("."));
                        if ((commandPackage == null
                                || className.startsWith(commandPackage))
                                && !className.startsWith(XLAB_SPI_PACKAGE)) {
                            classNames.add(className);
                        }
                    }

                } else {
                    listClassNames(classNames, item, commandPackage);
                }
            }
        }
    }

    private static boolean checkProperties(Properties prop) {
        String name = prop.getProperty("plugin-name");
        String namespace = prop.getProperty("plugin-namespace");
        String commandPackage = prop.getProperty("plugin-command-package");
        boolean flag = true;
        if (StringUtils.isblank(name)) {
            System.out.println("xpocket.def plugin-name is empty");
            flag = false;
        }

        if (StringUtils.isblank(namespace)) {
            System.out.println("xpocket.def plugin-namespace is empty");
            flag = false;
        }

        if (StringUtils.isblank(commandPackage)) {
            System.out.println("xpocket.def plugin-command-package is empty");
            flag = false;
        }
        return flag;
    }

    @Override
    public String name() {
        return null;
    }
}
