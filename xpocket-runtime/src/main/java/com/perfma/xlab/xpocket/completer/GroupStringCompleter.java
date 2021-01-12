package com.perfma.xlab.xpocket.completer;

import java.util.Collection;
import org.jline.reader.Candidate;
import org.jline.reader.impl.completer.StringsCompleter;
import org.jline.utils.AttributedString;

/**
 *
 * @author gongyu <yin.tong@perfma.com>
 */
public class GroupStringCompleter extends StringsCompleter {

    public GroupStringCompleter(Iterable<String> strings,String groupName) {
        assert strings != null;
        for (String string : strings) {
            candidates.add(new Candidate(AttributedString.stripAnsi(string), string, groupName, null, null, null, true));
        }
    }

    public GroupStringCompleter(Collection<Candidate> candidates) {
        super(candidates);
    }

}
