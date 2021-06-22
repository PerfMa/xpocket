package com.perfma.xlab.xpocket.framework.spi.impl.agentlauncher;

import com.perfma.xlab.xpocket.plugin.ui.UIEngine;
import com.perfma.xlab.xpocket.utils.XPocketConstants;
import com.sun.tools.attach.VirtualMachine;
import com.sun.tools.attach.VirtualMachineDescriptor;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.Writer;
import static java.lang.Thread.interrupted;
import java.lang.instrument.Instrumentation;
import java.net.URLEncoder;
import jline.UnixTerminal;
import jline.console.ConsoleReader;
import org.apache.commons.net.telnet.TelnetClient;

/**
 *
 * @author gongyu <yin.tong@perfma.com>
 */
public class AgentLauncherUIEngineImpl extends AgentLauncherNamedObject implements UIEngine {

    private static final String OS_NAME = System.getProperty("os.name");
    
    private static final String XPOCKET_HOME = System.getProperty("XPOCKET_HOME");

    private static final String XPOCKET_PLUGIN_PATH = System.getProperty("XPOCKET_PLUGIN_PATH");

    private static final String XPOCKET_CONFIG_PATH = System.getProperty("XPOCKET_CONFIG_PATH");

    private static final String XPOCKET_AGENT_JAR = "xpocket-agent-" + XPocketConstants.VERSION + ".jar";
    
    private static final int[] triggle = new int[]{27,79,66};

    private static final int DEFALUT_PORT = 9527;

    static {
        //windows
        if(OS_NAME.toLowerCase().contains("win")) {
            triggle[1] = 91;
        }
    }
    
    @Override
    public void start(String[] def, String[] args, Instrumentation inst) {

        String pid = "";
        int port = DEFALUT_PORT;

        if (args != null && args.length >= 2) {
            for (int i = 0; i < args.length; i++) {
                String arg = args[i];
                if ("-port".equals(arg)) {
                    i++;
                    port = Integer.parseInt(args[i]);
                } else if ("-pid".equals(arg)) {
                    i++;
                    pid = args[i];
                }
            }
        }

        String agentPath = XPOCKET_HOME + "/agent/" + XPOCKET_AGENT_JAR;
        attach(pid, port, agentPath);
    }

    private static void attach(String pid, int port, String agentPath) {
        VirtualMachineDescriptor virtualMachineDescriptor = null;
        for (VirtualMachineDescriptor descriptor : VirtualMachine.list()) {
            if (pid.equals(descriptor.id())) {
                virtualMachineDescriptor = descriptor;
                break;
            }
        }
        VirtualMachine virtualMachine = null;

        try {
            if (null == virtualMachineDescriptor) { // 使用 attach(String pid) 这种方式
                virtualMachine = VirtualMachine.attach(pid);
            } else {
                virtualMachine = VirtualMachine.attach(virtualMachineDescriptor);
            }

            String loadOption = "XPOCKET_HOME=%s;XPOCKET_PLUGIN_PATH=%s;XPOCKET_CONFIG_PATH=%s;port=%s";
            loadOption = String.format(loadOption,
                    URLEncoder.encode(XPOCKET_HOME, "UTF-8"),
                    URLEncoder.encode(XPOCKET_PLUGIN_PATH, "UTF-8"),
                    URLEncoder.encode(XPOCKET_CONFIG_PATH, "UTF-8"), port);

            virtualMachine.loadAgent(agentPath, loadOption);

            startTelnet("127.0.0.1", port);
        } catch (Throwable ex) {
            ex.printStackTrace();
        }
    }

    private static void startTelnet(String ip, int port) throws IOException {

        final ConsoleReader consoleReader = new ConsoleReader(System.in, System.out);
        consoleReader.setHandleUserInterrupt(true);
        jline.Terminal terminal = consoleReader.getTerminal();

        // support catch ctrl+c event
        terminal.disableInterruptCharacter();
        if (terminal instanceof UnixTerminal) {
            ((UnixTerminal) terminal).disableLitteralNextCharacter();
        }

        final TelnetClient telnet = new TelnetClient();
        telnet.setConnectTimeout(5000);

        try {
            telnet.connect(ip, port);
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(1);
        }

        readWrite(telnet, telnet.getInputStream(),telnet.getOutputStream(),
                consoleReader.getInput(),consoleReader.getOutput());
    }

    public static final void readWrite(TelnetClient telnet, final InputStream remoteInput, final OutputStream remoteOutput,
            final InputStream localInput, final Writer localOutput) {
        Thread reader, writer;

        reader = new Thread() {
            @Override
            public void run() {
                int ch;

                try {
                    while (!interrupted() && (ch = localInput.read()) != -1) {
                        remoteOutput.write(ch);
                        remoteOutput.flush();
                        if(ch == 13) {
                            remoteOutput.write(triggle[0]);
                            remoteOutput.write(triggle[1]);
                            remoteOutput.write(triggle[2]);
                            remoteOutput.flush();
                        }
                        
                    }
                } catch (IOException e) {
                    // e.printStackTrace();
                }
            }
        };

        writer = new Thread() {
            @Override
            public void run() {
                char[] cbuf = new char[1024];
                try {
                    InputStreamReader reader = new InputStreamReader(remoteInput);
                    while (true) {
                        int length = reader.read(cbuf);
                        if (length == -1 || length == 0) {
                            break;
                        }
                        localOutput.write(cbuf,0,length);
                        localOutput.flush();
                    }
                } catch (Throwable e) {
                    try {
                        localOutput.write(e.getMessage());
                    } catch (Throwable ex) {
                        ex.printStackTrace();
                    }
                }
            }
        };

        writer.setPriority(Thread.currentThread().getPriority() + 1);

        writer.start();
        reader.setDaemon(true);
        reader.start();

        try {
            writer.join();
            reader.interrupt();
        } catch (InterruptedException e) {
            // Ignored
        }
    }

}
