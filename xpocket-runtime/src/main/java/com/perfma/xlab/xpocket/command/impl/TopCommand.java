package com.perfma.xlab.xpocket.command.impl;

import com.perfma.xlab.xpocket.spi.command.CommandInfo;
import com.perfma.xlab.xpocket.spi.process.XPocketProcess;
import org.jline.keymap.BindingReader;
import org.jline.keymap.KeyMap;
import org.jline.terminal.Attributes;
import org.jline.terminal.Size;
import org.jline.terminal.Terminal;
import org.jline.utils.AttributedString;
import org.jline.utils.Display;
import org.jline.utils.InfoCmp.Capability;
import org.jline.utils.NonBlockingReader;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.LockSupport;

/**
 * @author xinxian
 * @create 2020-09-08 14:36
 **/
@CommandInfo(name = "top", usage = "system command top", index = 2)
public class TopCommand extends AbstractSystemCommand {

    private static final int MAC_HEAD = 12;

    private static StringBuilder builder = new StringBuilder("Input command (control + c) to exit");
    private static final int SPACE_SIZE = 530;

    @Override
    public void invoke(XPocketProcess process) {
        for(int i = 0; i < SPACE_SIZE; i ++){
            builder.append(" ");
        }
        String cmd = process.getCmd();
        StringBuilder builder = new StringBuilder(cmd);
        if(isLinux()){
            builder.append(" -b");
        }
        if(process.getArgs() != null){
            for(String s : process.getArgs()){
                builder.append(" ").append(s);
            }
        }
        display(builder.toString());
        process.end();
    }

    public void display(String cmd) {
        Terminal terminal = getReader().getTerminal();
        BindingReader bindingReader = new BindingReader(terminal.reader());

        Attributes attr = terminal.enterRawMode();
        Process p = null;
        Display display = null;
        try {
            // Use alternate buffer
            if (!terminal.puts(Capability.enter_ca_mode)) {
                terminal.puts(Capability.clear_screen);
            }
            terminal.puts(Capability.keypad_xmit);
            terminal.puts(Capability.cursor_invisible);
            terminal.writer().flush();

            display = new Display(terminal, true);
            KeyMap<String> keys = new KeyMap<>();
            keys.bind("Quit", "q", "Q");
            String op = null;
            p = Runtime.getRuntime().exec(cmd);


            do {
                Size size = new Size();
                size.copy(terminal.getSize());
                display.resize(size.getRows(), size.getColumns());

                List<AttributedString> lines = runNative(p);
                if(lines.size() > 0) {
                    display.clear();
                    display.update(lines, 0);
                }
                checkInterrupted();
                LockSupport.parkNanos(TimeUnit.NANOSECONDS.convert(1, TimeUnit.SECONDS));
                int ch = bindingReader.peekCharacter(20);
                if (ch == -1) {
                    op = "Quit";
                } else if (ch != NonBlockingReader.READ_EXPIRED) {
                    op = bindingReader.readBinding(keys, null, false);
                }
            } while (!"Quit".equals(op));

        } catch (Exception ex) {
            //do nothing
        } finally {
            if(display != null) {
                display.clear();
            }
            if(p != null){
                p.destroyForcibly();
            }
            terminal.setAttributes(attr);
            if (!terminal.puts(Capability.exit_ca_mode)) {
                terminal.puts(Capability.clear_screen);
            }
            terminal.puts(Capability.keypad_local);
            terminal.puts(Capability.cursor_visible);
            terminal.writer().flush();
        }
    }

    public List<AttributedString> runNative(Process p) {
        List<AttributedString> lines = new ArrayList<>();
        try {
            int i = 0;

            try {
                BufferedReader reader = new BufferedReader(new InputStreamReader(p.getInputStream()));
                String line;
                while((line = reader.readLine()) != null){
                    if(line.startsWith("top") || line.startsWith("Processes")
                        || line.startsWith("  procps")){
                        lines.add(new AttributedString(line));
                        break;
                    }
                }
                while ((line = reader.readLine()) != null) {
                    if (i++ > MAC_HEAD + 12) {
                        break;
                    }
                    lines.add(new AttributedString(line));

                }
                if(lines.size() > 0) {
                    lines.add(new AttributedString(builder.toString()));
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return lines;
    }

    /**
     * This is for long running commands to be interrupted by ctrl-c
     */
    private void checkInterrupted() throws InterruptedException {
        Thread.yield();
        if (Thread.currentThread().isInterrupted()) {
            throw new InterruptedException();
        }
    }

    @Override
    public boolean isAvailableNow(String cmd) {
        return isLinux() || isMacOS() || isMacOSX();
    }

    public boolean isLinux() {
        return OS.contains("linux");
    }

    public boolean isMacOS() {
        return OS.contains("mac") && OS.indexOf("os") > 0 && OS.indexOf("x") < 0;
    }

    public boolean isMacOSX() {
        return OS.contains("mac") && OS.indexOf("os") > 0 && OS.indexOf("x") > 0;
    }

    private static final String OS = System.getProperty("os.name").toLowerCase();
}
