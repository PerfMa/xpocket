package com.perfma.xlab.xpocket.framework.spi.impl.telnet;

import com.perfma.xlab.xpocket.framework.spi.impl.agent.XPocketAgentShellProvider;
import com.perfma.xlab.xpocket.framework.spi.impl.once.OnceNamedObject;
import com.perfma.xlab.xpocket.plugin.ui.UIEngine;
import java.lang.instrument.Instrumentation;
import org.jline.builtins.telnet.Telnet;
import org.jline.terminal.Terminal;
import org.jline.terminal.TerminalBuilder;

/**
 *
 * @author gongyu <yin.tong@perfma.com>
 */
public class TelnetUIEngineImpl extends TelnetNamedObject implements UIEngine {

    private static final String DEFAULT_TELNET_PORT = "9528";
    
    @Override
    public void start(String[] def, String[] args, Instrumentation inst) {

        Telnet telnet;
        try {
            String port = DEFAULT_TELNET_PORT;
            for(int i=0;i<args.length;i++) {
                String arg = args[i];
                if("-port".equals(arg)) {
                    i++;
                    port = args[i];
                }
            }
            
            Terminal terminal = TerminalBuilder.builder()
                    .nativeSignals(true)
                    .signalHandler(Terminal.SignalHandler.SIG_IGN)
                    .dumb(true).build();

            XPocketAgentShellProvider sp = new XPocketAgentShellProvider(false,inst);
            
            telnet = new Telnet(terminal, sp);
            sp.setTelnetd(telnet);
            telnet.telnetd(new String[]{"telnetd", "-p", port, "start"});

        } catch (Throwable ex) {
            ex.printStackTrace();
        }
    }
}
