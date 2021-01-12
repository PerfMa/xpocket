package com.perfma.xlab.xpocket.command.impl;

import com.perfma.xlab.xpocket.console.EndOfInputException;
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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.LockSupport;

/**
 * @author xinxian
 * @create 2020-09-08 14:36
 **/
@CommandInfo(name = "top", usage = "like linux top info", index = 2)
public class TopCommand extends AbstractSystemCommand {

    private static final int MAC_HEAD = 12;

    @Override
    public void invoke(XPocketProcess process) {
        display("top -b -n 1");
        process.end();
    }

    public void display(String cmd) {
        Terminal terminal = getReader().getTerminal();
        BindingReader bindingReader = new BindingReader(terminal.reader());
        Size size = new Size();
        size.copy(terminal.getSize());

        Attributes attr = terminal.enterRawMode();
        try {
            // Use alternate buffer
            if (!terminal.puts(Capability.enter_ca_mode)) {
                terminal.puts(Capability.clear_screen);
            }
            terminal.puts(Capability.keypad_xmit);
            terminal.puts(Capability.cursor_invisible);
            terminal.writer().flush();

            Display display = new Display(terminal, true);
            KeyMap<String> keys = new KeyMap<>();
            keys.bind("Quit", "q", "Q");
            String op = null;
            do {
                display.clear();
                display.resize(size.getRows(), size.getColumns());
                List<AttributedString> lines = runNative(cmd);
                display.update(lines, 0);
                checkInterrupted();
                LockSupport.parkNanos(TimeUnit.NANOSECONDS.convert(1, TimeUnit.SECONDS));
                int ch = bindingReader.peekCharacter(20);
                if (ch == -1) {
                    op = "Quit";
                } else if (ch != NonBlockingReader.READ_EXPIRED) {
                    op = bindingReader.readBinding(keys, null, false);
                }
            } while (!"Quit".equals(op));

        } catch (EndOfInputException | InterruptedException ex) {
            //do nothing
        } finally {
            terminal.setAttributes(attr);
            if (!terminal.puts(Capability.exit_ca_mode)) {
                terminal.puts(Capability.clear_screen);
            }
            terminal.puts(Capability.keypad_local);
            terminal.puts(Capability.cursor_visible);
            terminal.writer().flush();
        }
    }

    public List<AttributedString> runNative(String cmd) {
        Process p = null;
        List<AttributedString> lines = new ArrayList<>();
        try {
            if (System.getProperty("os.name").toLowerCase().contains("linux")) {
                p = Runtime.getRuntime().exec(cmd);
            } else {
                ProcessBuilder ps = new ProcessBuilder("top");
                ps.redirectErrorStream(true);
                p = ps.start();
            }
            int i = 0;
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(p.getInputStream()))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    lines.add(new AttributedString(line));
                    // TODO
                    if (i++ == MAC_HEAD + 10) {
                        break;
                    }
                }
                lines.add(new AttributedString(""));
                lines.add(new AttributedString(" Input command (control + c) to exit"));
                p.destroyForcibly();
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
