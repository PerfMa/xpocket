package com.perfma.xlab.xpocket.completer;

import com.perfma.xlab.xpocket.framework.spi.impl.XPocketStatusContext;
import java.util.Collection;
import java.util.List;
import org.jline.reader.Candidate;
import org.jline.reader.LineReader;
import org.jline.reader.ParsedLine;
import com.perfma.xlab.xpocket.spi.context.PluginBaseInfo;

/**
 *
 * @author gongyu <yin.tong@perfma.com>
 */
public class CommandCompleter extends GroupStringCompleter {

    public CommandCompleter(Iterable<String> strings,String groupName) {
        super(strings,groupName);
    }

    public CommandCompleter(Collection<Candidate> candidates) {
        super(candidates);
    }

    @Override
    public void complete(LineReader reader, ParsedLine commandLine, 
            List<Candidate> candidates) {
        PluginBaseInfo pluginContext = XPocketStatusContext.instance.currentPlugin();
        String currentGroup = pluginContext == null 
                ? null 
                : String.format("%s@%s", pluginContext.getName(),
                        pluginContext.getNamespace().toUpperCase());
        int index = commandLine.wordIndex();
        String lastWord = commandLine.word();
        String lastPreWord = commandLine.wordIndex() > 0
                ? commandLine.words().get(index - 1)
                : null;
        if(index == 0 || lastWord.contains("|") || lastPreWord.endsWith("|") ) {
            super.complete(reader, commandLine, candidates);   
            for(int i=0;i<candidates.size();i++) {
                Candidate candi = candidates.get(i);
                String value = candi.value();
                String displ = candi.displ();
                String group = candi.group();
                boolean flag = false;
                if(currentGroup != null && group != null
                        && group.endsWith(currentGroup)) {
                    value = value.substring(value.indexOf(".") + 1, value.length());
                    displ = value;
                    group = "CURRENT-PLUGIN : " + currentGroup;
                    flag = true;
                }
                
                if(lastWord.contains("|")) {
                    String prefix = lastWord.substring(0,lastWord.indexOf('|') + 1);
                    value = prefix + value;
                    flag = true;
                }
                
                if(flag) {
                    candidates.set(i,new Candidate(value, displ,group, candi.descr(),
                                candi.suffix(), candi.key(), candi.complete()));
                }    
            }
        }
    }
}
