package com.perfma.xlab.xpocket.plugin.adaptor;

import com.perfma.xlab.xpocket.spi.command.AbstractXPocketCommand;
import com.perfma.xlab.xpocket.spi.context.SessionContext;
import com.perfma.xlab.xpocket.spi.process.XPocketProcess;
import com.perfma.xlab.xpocket.spi.process.XPocketProcessAction;
import com.perfma.xlab.xpocket.utils.XPocketConstants;
import com.sun.tools.attach.VirtualMachine;
import com.sun.tools.attach.VirtualMachineDescriptor;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLEncoder;
import org.apache.commons.net.telnet.TelnetClient;
import org.apache.commons.net.telnet.TelnetNotificationHandler;

/**
 *
 * @author gongyu <yin.tong@perfma.com>
 */
public class JavaAgentCommandAdaptor extends AbstractXPocketCommand implements Runnable, TelnetNotificationHandler {

    private static final String XPOCKET_HOME = System.getProperty("XPOCKET_HOME");

    private static final String XPOCKET_PLUGIN_PATH = System.getProperty("XPOCKET_PLUGIN_PATH");

    private static final String XPOCKET_CONFIG_PATH = System.getProperty("XPOCKET_CONFIG_PATH");

    private static final String XPOCKET_AGENT_JAR = "xpocket-agent-" + XPocketConstants.VERSION + ".jar";

    private static final int DEFALUT_PORT = 9527;

    private static final byte CTRL_C = 0x03;

    private final TelnetClient telnet = new TelnetClient();

    private boolean attachStatus = false;

    private XPocketProcess process;

    @Override
    public void invoke(XPocketProcess process, SessionContext context) throws Throwable {

        this.process = process;
        String cmd = process.getCmd();
        String[] args = process.getArgs();

        process.register(new XPocketProcessAction() {
            @Override
            public void userInput(String input) throws Throwable {
            }

            @Override
            public void interrupt() throws Throwable {
                telnet.getOutputStream().write(CTRL_C);
                telnet.getOutputStream().flush();
            }

        });

        if (attachStatus) {
            telnet.getOutputStream().write(handleCmd(cmd, args));
            telnet.getOutputStream().flush();
        } else {
            process.output("Please use system.attach to attach a Java process before use this plugin!");
            process.end();
        }

    }

    public String detach(XPocketProcess process) throws IOException {
        telnet.disconnect();
        this.attachStatus = false;
        process.end();
        return "OK";
    }

    public String attach(XPocketProcess process, String pid) {
        try {
            this.process = process;
            int port = DEFALUT_PORT;
            String agentPath = XPOCKET_HOME + "/agent/" + XPOCKET_AGENT_JAR;
            return attach(pid, port, agentPath);
        } finally {
            this.process = null;
        }
    }

    private String attach(String pid, int port, String agentPath) {
        VirtualMachineDescriptor virtualMachineDescriptor = null;
        for (VirtualMachineDescriptor descriptor : VirtualMachine.list()) {
            if (pid.equals(descriptor.id())) {
                virtualMachineDescriptor = descriptor;
                break;
            }
        }
        VirtualMachine virtualMachine;

        try {
            if (null == virtualMachineDescriptor) { // 使用 attach(String pid) 这种方式
                virtualMachine = VirtualMachine.attach(pid);
            } else {
                virtualMachine = VirtualMachine.attach(virtualMachineDescriptor);
            }

            if(virtualMachine == null) {
                return pid + " not found!";
            }
            
            String loadOption = "XPOCKET_HOME=%s;XPOCKET_PLUGIN_PATH=%s;XPOCKET_CONFIG_PATH=%s;port=%s;XPOCKET_ONLY_AGENT=true";
            loadOption = String.format(loadOption,
                    URLEncoder.encode(XPOCKET_HOME, "UTF-8"),
                    URLEncoder.encode(XPOCKET_PLUGIN_PATH, "UTF-8"),
                    URLEncoder.encode(XPOCKET_CONFIG_PATH, "UTF-8"), port);

            virtualMachine.loadAgent(agentPath, loadOption);

            startTelnet("127.0.0.1", port);
            this.attachStatus = true;
        } catch (Throwable ex) {
            return ex.getMessage();
        }

        return "OK";
    }

    private void startTelnet(String host, int port) throws IOException {
        telnet.connect(host, port);
        Thread reader = new Thread(this);
        reader.start();
    }

    @Override
    public void run() {
        InputStream instr = telnet.getInputStream();

        try {
            int ret_read = 0, index = 0;
            char[] line = new char[1024];

            LOOP:
            for (;;) {
                ret_read = instr.read();

                if (ret_read == -1) {
                    break;
                }

                if (process == null) {
                    continue;
                }

                switch (ret_read) {
                    case '\n':
                        String lineStr = new String(line, 0, index);
                        if (!lineStr.trim().equalsIgnoreCase(process.getCmd())) {
                            process.output(lineStr + "\n");
                        }
                        index = 0;
                        break;
                    case '>':
                        line = put(line, index++, (char) ret_read);
                        String flag = new String(line, 0, index);
                        if (flag.contains("XPocket [")) {
                            process.end();
                            process = null;
                            index = 0;
                        }
                        break;
                    default:
                        line = put(line, index++, (char) ret_read);
                }

            }
        } catch (IOException e) {
            process.output("Exception while reading socket:" + e.getMessage());
        }

        try {
            telnet.disconnect();
        } catch (IOException e) {
            System.out.println("Exception while closing telnet:" + e.getMessage());
        }
    }

    private char[] put(char[] b, int index, char content) {
        char[] result = b;

        if (b.length < index && b.length * 2 > index) {
            result = new char[b.length * 2];
            System.arraycopy(b, 0, result, 0, b.length);
        } else if (b.length * 2 < index) {
            result = new char[index + 1];
            System.arraycopy(b, 0, result, 0, b.length);
        }
        result[index] = content;
        return result;
    }

    @Override
    public void receivedNegotiation(int negotiation_code, int option_code) {
    }

    private byte[] handleCmd(String cmd, String[] args) {
        StringBuilder cmdStr = new StringBuilder(cmd).append(' ');

        if (args != null) {
            for (String arg : args) {
                cmdStr.append(arg).append(' ');
            }
        }

        cmdStr.append("\n");

        return cmdStr.toString().getBytes();
    }

}
