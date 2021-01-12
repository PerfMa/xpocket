package com.perfma.xlab.xpocket.completer;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.jline.reader.Candidate;
import org.jline.reader.Completer;
import org.jline.reader.LineReader;
import org.jline.reader.ParsedLine;

/**
 * ParamsCompleter
 *
 * @author gongyu <yin.tong@perfma.com>
 */
public class ParamsCompleter implements Completer {

    private final Map<String, List<Completer>> paramCompleters = new ConcurrentHashMap<>();

    @Override
    public void complete(LineReader reader, ParsedLine line, List<Candidate> candidates) {
        if (line.wordIndex() > 0) {
            String command = "";
            int index = line.wordIndex();
            int commandIndex = -1;
            for (int i = index; i >= 0; i--) {
                String word = line.words().get(i);
                if (word.contains("|")) {
                    if (word.endsWith("|")) {
                        commandIndex = i + 1;
                    } else {
                        commandIndex = i;
                    }

                    if (commandIndex >= index) {
                        return;
                    } else {
                        command = line.words().get(commandIndex);
                    }

                    if(command.contains("|")) {
                        command = command.substring(command.indexOf("|") + 1);
                    }
                    
                    break;
                }

                if (i == 0) {
                    commandIndex = 0;
                    command = line.words().get(0);
                }
            }
            List<Completer> completers = paramCompleters.get(command);
            if (completers != null && completers.size() >= index - commandIndex) {
                completers.get(index - commandIndex - 1).complete(reader, line, candidates);
            }
        }
    }

    public void registry(String plugin, List<Completer> completers) {
        paramCompleters.put(plugin, completers);
    }

    public void registry(String plugin, Completer completers) {
        if (!paramCompleters.containsKey(plugin)) {
            paramCompleters.put(plugin, new ArrayList<>());
        }
        paramCompleters.get(plugin).add(completers);
    }

}
