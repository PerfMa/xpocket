package com.perfma.xlab.xpocket.framework.spi.impl.agent;

import com.perfma.xlab.xpocket.plugin.ui.UIEngine;
import java.lang.instrument.Instrumentation;
import org.jline.builtins.telnet.Telnet;
import org.jline.terminal.Terminal;
import org.jline.terminal.TerminalBuilder;

/**
 *
 * @author gongyu <yin.tong@perfma.com>
 */
public class AgentUIEngineImpl extends AgentNamedObject implements UIEngine {

    private static final String DEFAULT_TELNET_PORT = "9527";
    
    @Override
    public void start(String[] def, String[] args, Instrumentation inst) {

        Telnet telnet;
        try {
            String port = DEFAULT_TELNET_PORT;
            boolean isOnLoad = false;
            for(int i=0;i<args.length;i++) {
                String arg = args[i];
                if("-port".equals(arg)) {
                    i++;
                    port = args[i];
                } else if ("-onload".equals(arg)) {
                    i++;
                    isOnLoad = Boolean.valueOf(args[i]);
                }
            }
            
            Terminal terminal = TerminalBuilder.builder()
                    .nativeSignals(true)
                    .signalHandler(Terminal.SignalHandler.SIG_IGN)
                    .dumb(true).build();

            XPocketAgentShellProvider sp = new XPocketAgentShellProvider(isOnLoad,inst);
            
            telnet = new Telnet(terminal, sp);
            sp.setTelnetd(telnet);
            telnet.telnetd(new String[]{"telnetd", "-p", port, "start"});

        } catch (Throwable ex) {
            ex.printStackTrace();
        }
    }
}
