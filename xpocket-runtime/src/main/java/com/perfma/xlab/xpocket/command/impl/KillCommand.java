package com.perfma.xlab.xpocket.command.impl;

import com.perfma.xlab.xpocket.command.AbstractSystemCommand;
import com.perfma.xlab.xpocket.spi.command.CommandInfo;
import com.perfma.xlab.xpocket.spi.command.XPocketProcessTemplate;
import com.perfma.xlab.xpocket.spi.process.XPocketProcess;
import com.perfma.xlab.xpocket.utils.TerminalUtil;
import java.io.IOException;

/**
 * @author: TuGai
 *
 */
@CommandInfo(name = "kill", usage = "kill target java processor", index = 99)
public class KillCommand extends AbstractSystemCommand {

    private static String[] killCmds =new String[0];
    
    static {
        if(TerminalUtil.OS_NAME.toLowerCase().contains("win")) {
            killCmds = new String[]{"taskkill", "/F", "/PID",null};
        } else {
            killCmds = new String[]{"kill", "-9", null};
        }
    }
    
    @Override
    public void invoke(XPocketProcess process) {
        XPocketProcessTemplate.execute(process,
                (String cmd, String[] args)
                -> {
            String[] cmds = new String[killCmds.length];
            System.arraycopy(killCmds, 0, cmds, 0, cmds.length);
            cmds[cmds.length - 1] = args[0];
            ProcessBuilder builder = new ProcessBuilder(cmds).inheritIO();
            Process p = builder.start();
            p.waitFor();
            return String.format("Process: %s killed!",args[0]);
        });
    }
    
    public static void main(String[] args) throws InterruptedException, IOException {
        ProcessBuilder builder = new ProcessBuilder(new String[]{"taskkill", "/F", "/PID","3272"}).inheritIO();
        Process p = builder.start();
        p.waitFor();
        
    }
}
