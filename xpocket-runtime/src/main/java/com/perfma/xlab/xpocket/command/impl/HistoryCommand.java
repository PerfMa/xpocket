package com.perfma.xlab.xpocket.command.impl;

import com.perfma.xlab.xpocket.spi.command.CommandInfo;
import com.perfma.xlab.xpocket.spi.process.XPocketProcess;
import org.jline.reader.History;

import java.util.ListIterator;

/**
 * @author xinxian
 * @create 2020-11-02 20:33
 **/
@CommandInfo(name = "history", usage = "list command history", index = 14)
public class HistoryCommand extends AbstractSystemCommand {

    @Override
    public void invoke(XPocketProcess process) throws Throwable {
        History history = getReader().getHistory();
        final ListIterator<History.Entry> iterator = history.iterator();
        while (iterator.hasNext()) {
            process.output(iterator.next().toString());
        }
        process.end();
    }
}
