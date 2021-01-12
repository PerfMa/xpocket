package com.perfma.xlab.xpocket.command.impl;

import com.perfma.xlab.xpocket.localjvm.JvmPid;
import com.perfma.xlab.xpocket.spi.command.CommandInfo;
import com.perfma.xlab.xpocket.spi.process.XPocketProcess;
import com.sun.tools.attach.VirtualMachine;
import com.sun.tools.attach.VirtualMachineDescriptor;

import java.util.*;

/**
 * @author xinxian
 * @create 2020-09-08 14:35
 **/
@CommandInfo(name = "jps", usage = "list available java process，under linux, you can use the \"ps\" command to list all processes", index = 8)
public class JPSCommand extends AbstractSystemCommand {

    @Override
    public void invoke(XPocketProcess process) {
        List<String> out = new LinkedList<String>();
        for (JvmPid jvmPid : getLocalProcessList()) {
            out.add("@|yellow " + jvmPid.getPid() + "|@ @|white " + jvmPid.getCommand() + " |@");
        }
        for (String s : out) {
            process.output(s);
        }

        process.end();
    }


    public Collection<JvmPid> getLocalProcessList() {
        List<VirtualMachineDescriptor> localVms = VirtualMachine.list();
        Set<JvmPid> out = new HashSet<>();
        for (VirtualMachineDescriptor vmd : localVms) {
            out.add(new JvmPid(vmd.id(), vmd.displayName()));
        }
        return out;
    }

}
