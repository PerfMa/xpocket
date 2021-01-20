package com.perfma.xlab.xpocket.framework.spi.impl;

import com.perfma.xlab.xpocket.plugin.command.CommandLoader;
import com.perfma.xlab.xpocket.plugin.context.FrameworkPluginContext;

import java.util.*;

/**
 * @author gongyu <yin.tong@perfma.com>
 */
public class DefaultCommandLoader extends DefaultNamedObject implements CommandLoader {

    private static final TreeMap<String, FrameworkPluginContext> cmdIndex = new TreeMap<>();

    private static final TreeMap<String, Set<String>> cmdTable = new TreeMap<>();

    @Override
    public Map<String, Set<String>> getCmdTable() {
        return Collections.unmodifiableMap(cmdTable);
    }

    @Override
    public FrameworkPluginContext findByCommand(String command) {
        return cmdIndex.get(command);
    }

    @Override
    public boolean addCommand(Set<FrameworkPluginContext> contexts) {

        HashSet<String> tempRepeateIndex = new HashSet<>();
        contexts.forEach(pc -> {
            final String namespace = pc.getNamespace();
            String name = pc.getName();
            Set<String> commands = pc.getCommands();
            cmdTable.put(String.format("%s@%s", name,namespace.toUpperCase()),
                    commands);
            commands.forEach(command
                    -> {
                String shortName = String.format("%s.%s",name,command);
                String completeName = String.format("%s.%s@%s", name, command,namespace);
                
                //command index
                if(tempRepeateIndex.contains(command)) {
                    cmdIndex.remove(command);
                } else {
                    tempRepeateIndex.add(command);
                    cmdIndex.put(command, pc);
                }
                
                //shortname index
                if(tempRepeateIndex.contains(shortName)) {
                    cmdIndex.remove(shortName);
                } else {
                    tempRepeateIndex.add(shortName);
                    cmdIndex.put(shortName, pc);
                }
                
                cmdIndex.put(completeName, pc);
                
            });
        });

        return true;
    }

    @Override
    public Set<String> cmdSet() {
        return cmdIndex.keySet();
    }

}
