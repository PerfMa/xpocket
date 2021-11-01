package com.perfma.xlab.xpocket.command.impl;

import com.perfma.xlab.xpocket.command.AbstractSystemCommand;
import com.perfma.xlab.xpocket.spi.command.CommandInfo;
import com.perfma.xlab.xpocket.spi.command.XPocketProcessTemplate;
import com.perfma.xlab.xpocket.spi.command.callback.SimpleProcessCallback;
import com.perfma.xlab.xpocket.spi.process.XPocketProcess;
import com.perfma.xlab.xpocket.utils.StringUtils;
import org.jline.builtins.TTop;
import org.jline.terminal.Terminal;

/**
 * @author gongyu <yin.tong@perfma.com>
 */
@CommandInfo(name = "ttop", usage = "top info for XPocket")
public class TTopCommand extends AbstractSystemCommand {

    private final String usage =
            " ttop -  display and update sorted information about threads\n" +
                    " Usage: ttop [OPTIONS]\n" +
                    "   -? --help                    Show help\n" +
                    "   -o --order=ORDER             Comma separated list of sorting keys\n" +
                    "   -t --stats=STATS             Comma separated list of stats to display\n" +
                    "   -s --seconds=SECONDS         Delay between updates in seconds\n" +
                    "   -m --millis=MILLIS           Delay between updates in milliseconds\n" +
                    "   -n --nthreads=NTHREADS       Only display up to NTHREADS threads";

    @Override
    public String details(String cmd) {
        return usage;
    }

    @Override
    public void invoke(XPocketProcess process) throws Throwable {
        XPocketProcessTemplate.execute(process, new SimpleProcessCallback() {
            @Override
            public String call(String cmd, String[] args) throws Throwable {
                Terminal terminal = getReader().getTerminal();
                TTop.ttop(terminal, System.out, System.err, args);
                return "";
            }
        });
    }

    @Override
    public boolean isAvailableNow(String cmd) {
        final String ttop = System.getProperty("ttop");
        return !StringUtils.isblank(ttop) && "true".equalsIgnoreCase(ttop);
    }
}
