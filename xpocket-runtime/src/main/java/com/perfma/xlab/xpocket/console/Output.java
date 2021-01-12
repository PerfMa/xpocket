package com.perfma.xlab.xpocket.console;

import java.io.Closeable;
import java.io.IOException;
import java.io.PrintStream;
import java.util.regex.Pattern;
import org.fusesource.jansi.AnsiConsole;

import static org.fusesource.jansi.Ansi.ansi;

/**
 * @author xinxian
 * @create 2020-09-08 14:38
 **/
public class Output implements Closeable {
    private final Pattern ansiRemovePattern = Pattern.compile("(@\\|\\w* )|( ?\\|@)");
    private final PrintStream out;
    private boolean useColors = true, ansiInstalled, filterInfo;

    public Output(PrintStream out, boolean useColors) {
        this.out = out;
        setUseColors(useColors);
    }

    public void setFilterInfo(boolean filterInfo) {
        this.filterInfo = filterInfo;
    }

    public boolean isUseColors() {
        return useColors;
    }


    public void setUseColors(boolean useColors) {
        this.useColors = useColors;
        if (useColors) {
            AnsiConsole.systemInstall();
            ansiInstalled = true;
        } else {
            AnsiConsole.systemUninstall();
            ansiInstalled = false;
        }
    }


    public void print(String text) {
        if (text == null) {
            return;
        }
        if (useColors) {
            out.print(ansi().render(text));
        } else {
            String s = ansiRemovePattern.matcher(text).replaceAll("");
            out.print(s);
        }
        out.flush();
    }


    public void println(String text) {
        print(text);
        out.println();
        out.flush();
    }

    @Override
    public void close() throws IOException {
        if (ansiInstalled) {
            AnsiConsole.systemUninstall();
        }
        out.close();
    }

    public void outError(String text) {
        println("@|red " + text + "|@");
    }

    public void outInfo(String text) {
        if (!filterInfo) {
            println("@|cyan " + text + "|@");
        }
    }

}
