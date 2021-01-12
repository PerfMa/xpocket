package com.perfma.xlab.xpocket.localjvm;

import java.util.Objects;

/**
 * @author xinxian
 * @create 2020-09-10 10:29
 **/
public class JvmPid {
    private final String pid;
    private final String command;

    public JvmPid(String pid, String command) {
        this.pid = pid;
        this.command = command;
    }

    public String getPid() {
        return pid;
    }

    public String getCommand() {
        return command;
    }

    public String getFullName() {
        return pid + ":" + command;
    }

    public String getShortName() {
        int i = command.indexOf(' ');
        String shortCommand;
        if (i > 0) {
            shortCommand = command.substring(0, i);
        } else {
            shortCommand = command;
        }
        return pid + ":" + shortCommand;
    }

    @Override
    public String toString() {
        return "jvm(" + pid + ":" + command + ")";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        JvmPid jvmPid = (JvmPid) o;
        return Objects.equals(pid, jvmPid.pid) &&
                Objects.equals(command, jvmPid.command);
    }

    @Override
    public int hashCode() {
        return Objects.hash(pid, command);
    }
}
