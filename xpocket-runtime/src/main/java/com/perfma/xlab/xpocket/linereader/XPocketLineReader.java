package com.perfma.xlab.xpocket.linereader;

import org.jline.reader.*;
import org.jline.reader.impl.LineReaderImpl;
import org.jline.reader.impl.ReaderUtils;
import org.jline.terminal.Terminal;
import org.jline.utils.AttributedString;
import org.jline.utils.Levenshtein;
import org.jline.utils.Log;

import java.io.IOException;
import java.util.*;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * @author gongyu <yin.tong@perfma.com>
 */
public class XPocketLineReader extends LineReaderImpl {

    public XPocketLineReader(Terminal terminal) throws IOException {
        super(terminal);
    }

    boolean getBoolean(String name, boolean def) {
        return ReaderUtils.getBoolean(this, name, def);
    }

    int getInt(String name, int def) {
        return ReaderUtils.getInt(this, name, def);
    }

    String getString(String name, String def) {
        return ReaderUtils.getString(this, name, def);
    }

    private void pushBackBinding(boolean skip) {
        String s = getLastBinding();
        if (s != null) {
            bindingReader.runMacro(s);
            skipRedisplay = skip;
        }
    }

    private CompletingParsedLine wrap(ParsedLine line) {
        if (line instanceof CompletingParsedLine) {
            return (CompletingParsedLine) line;
        } else {
            return new CompletingParsedLine() {
                @Override
                public String word() {
                    return line.word();
                }

                @Override
                public int wordCursor() {
                    return line.wordCursor();
                }

                @Override
                public int wordIndex() {
                    return line.wordIndex();
                }

                @Override
                public List<String> words() {
                    return line.words();
                }

                @Override
                public String line() {
                    return line.line();
                }

                @Override
                public int cursor() {
                    return line.cursor();
                }

                @Override
                public CharSequence escape(CharSequence candidate, boolean complete) {
                    return candidate;
                }

                @Override
                public int rawWordCursor() {
                    return wordCursor();
                }

                @Override
                public int rawWordLength() {
                    return word().length();
                }
            };
        }
    }

    private Function<Map<String, List<Candidate>>, Map<String, List<Candidate>>> simpleMatcher(Predicate<String> pred) {
        return m -> m.entrySet().stream()
                .filter(e -> pred.test(e.getKey()))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }

    private Function<Map<String, List<Candidate>>, Map<String, List<Candidate>>> typoMatcher(String word, int errors, boolean caseInsensitive) {
        return m -> {
            Map<String, List<Candidate>> map = m.entrySet().stream()
                    .filter(e -> distance(word, caseInsensitive ? e.getKey() : e.getKey().toLowerCase()) < errors)
                    .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
            if (map.size() > 1) {
                map.computeIfAbsent(word, w -> new ArrayList<>())
                        .add(new Candidate(word, word, getOriginalGroupName(), null, null, null, false));
            }
            return map;
        };
    }

    private int distance(String word, String cand) {
        if (word.length() < cand.length()) {
            int d1 = Levenshtein.distance(word, cand.substring(0, Math.min(cand.length(), word.length())));
            int d2 = Levenshtein.distance(word, cand);
            return Math.min(d1, d2);
        } else {
            return Levenshtein.distance(word, cand);
        }
    }

    private String getCommonStart(String str1, String str2, boolean caseInsensitive) {
        int[] s1 = str1.codePoints().toArray();
        int[] s2 = str2.codePoints().toArray();
        int len = 0;
        while (len < Math.min(s1.length, s2.length)) {
            int ch1 = s1[len];
            int ch2 = s2[len];
            if (ch1 != ch2 && caseInsensitive) {
                ch1 = Character.toUpperCase(ch1);
                ch2 = Character.toUpperCase(ch2);
                if (ch1 != ch2) {
                    ch1 = Character.toLowerCase(ch1);
                    ch2 = Character.toLowerCase(ch2);
                }
            }
            if (ch1 != ch2) {
                break;
            }
            len++;
        }
        return new String(s1, 0, len);
    }


    @Override
    protected boolean doComplete(CompletionType lst, boolean useMenu, boolean prefix, boolean forSuggestion) {
        // If completion is disabled, just bail out
        if (getBoolean(DISABLE_COMPLETION, false)) {
            return true;
        }
        // Try to expand history first
        // If there is actually an expansion, bail out now
        if (!isSet(Option.DISABLE_EVENT_EXPANSION)) {
            try {
                if (expandHistory()) {
                    return true;
                }
            } catch (Exception e) {
                Log.info("Error while expanding history", e);
                return false;
            }
        }

        // Parse the command line
        CompletingParsedLine line;
        try {
            line = wrap(parser.parse(buf.toString(), buf.cursor(), Parser.ParseContext.COMPLETE));
        } catch (Exception e) {
            Log.info("Error while parsing line", e);
            return false;
        }

        // Find completion candidates
        List<Candidate> candidates = new ArrayList<>();
        try {
            if (completer != null) {
                completer.complete(this, line, candidates);
            }
        } catch (Exception e) {
            Log.info("Error while finding completion candidates", e);
            return false;
        }

        if (lst == CompletionType.ExpandComplete || lst == CompletionType.Expand) {
            String w = expander.expandVar(line.word());
            if (!line.word().equals(w)) {
                if (prefix) {
                    buf.backspace(line.wordCursor());
                } else {
                    buf.move(line.word().length() - line.wordCursor());
                    buf.backspace(line.word().length());
                }
                buf.write(w);
                return true;
            }
            if (lst == CompletionType.Expand) {
                return false;
            } else {
                lst = CompletionType.Complete;
            }
        }

        boolean caseInsensitive = isSet(Option.CASE_INSENSITIVE);
        int errors = getInt(ERRORS, DEFAULT_ERRORS);

        // Build a list of sorted candidates
        Map<String, List<Candidate>> sortedCandidates = new HashMap<>();
        for (Candidate cand : candidates) {
            sortedCandidates
                    .computeIfAbsent(AttributedString.fromAnsi(cand.value()).toString(), s -> new ArrayList<>())
                    .add(cand);
        }

        // Find matchers
        // TODO: glob completion
        List<Function<Map<String, List<Candidate>>,
                Map<String, List<Candidate>>>> matchers;
        Predicate<String> exact;
        if (prefix) {
            String wd = line.word();
            String wdi = caseInsensitive ? wd.toLowerCase() : wd;
            String wp = wdi.substring(0, line.wordCursor());
            matchers = Arrays.asList(
                    simpleMatcher(s -> (caseInsensitive ? s.toLowerCase() : s).startsWith(wp)),
                    simpleMatcher(s -> (caseInsensitive ? s.toLowerCase() : s).contains(wp)),
                    typoMatcher(wp, errors, caseInsensitive)
            );
            exact = s -> caseInsensitive ? s.equalsIgnoreCase(wp) : s.equals(wp);
        } else if (isSet(Option.COMPLETE_IN_WORD)) {
            String wd = line.word();
            String wdi = caseInsensitive ? wd.toLowerCase() : wd;
            String wp = wdi.substring(0, line.wordCursor());
            String ws = wdi.substring(line.wordCursor());
            Pattern p1 = Pattern.compile(Pattern.quote(wp) + ".*" + Pattern.quote(ws) + ".*");
            Pattern p2 = Pattern.compile(".*" + Pattern.quote(wp) + ".*" + Pattern.quote(ws) + ".*");
            matchers = Arrays.asList(
                    simpleMatcher(s -> p1.matcher(caseInsensitive ? s.toLowerCase() : s).matches()),
                    simpleMatcher(s -> p2.matcher(caseInsensitive ? s.toLowerCase() : s).matches()),
                    typoMatcher(wdi, errors, caseInsensitive)
            );
            exact = s -> caseInsensitive ? s.equalsIgnoreCase(wd) : s.equals(wd);
        } else {
            String wd = line.word();
            String wdi = caseInsensitive ? wd.toLowerCase() : wd;
            if (isSet(Option.EMPTY_WORD_OPTIONS) || wd.length() > 0) {
                matchers = Arrays.asList(
                        simpleMatcher(s -> (caseInsensitive ? s.toLowerCase() : s).startsWith(wdi)),
                        simpleMatcher(s -> (caseInsensitive ? s.toLowerCase() : s).contains(wdi)),
                        typoMatcher(wdi, errors, caseInsensitive)
                );
            } else {
                matchers = Arrays.asList(
                        simpleMatcher(s -> !s.startsWith("-"))
                );
            }
            exact = s -> caseInsensitive ? s.equalsIgnoreCase(wd) : s.equals(wd);
        }

        // Find matching candidates
        Map<String, List<Candidate>> matching = new HashMap<>();
        matching.putAll(matchers.get(0).apply(sortedCandidates));
        matching.putAll(matchers.get(1).apply(sortedCandidates));

        for (int i = 2; i < matchers.size(); i++) {
            Function<Map<String, List<Candidate>>,
                    Map<String, List<Candidate>>> matcher = matchers.get(i);
            if (!matching.isEmpty()) {
                break;
            }
            matching = matcher.apply(sortedCandidates);
        }

        // If we have no matches, bail out
        if (matching.isEmpty()) {
            return false;
        }
        size.copy(terminal.getSize());
        try {
            // If we only need to display the list, do it now
            if (lst == CompletionType.List) {
                List<Candidate> possible = matching.entrySet().stream()
                        .flatMap(e -> e.getValue().stream())
                        .collect(Collectors.toList());
                doList(possible, line.word(), false, line::escape, forSuggestion);
                return !possible.isEmpty();
            }

            // Check if there's a single possible match
            Candidate completion = null;
            // If there's a single possible completion
            if (matching.size() == 1) {
                completion = matching.values().stream().flatMap(Collection::stream)
                        .findFirst().orElse(null);
            }
            // Or if RECOGNIZE_EXACT is set, try to find an exact match
            else if (isSet(Option.RECOGNIZE_EXACT)) {
                completion = matching.values().stream().flatMap(Collection::stream)
                        .filter(Candidate::complete)
                        .filter(c -> exact.test(c.value()))
                        .findFirst().orElse(null);
            }
            // Complete and exit
            if (completion != null && !completion.value().isEmpty()) {
                if (prefix) {
                    buf.backspace(line.rawWordCursor());
                } else {
                    buf.move(line.rawWordLength() - line.rawWordCursor());
                    buf.backspace(line.rawWordLength());
                }
                buf.write(line.escape(completion.value(), completion.complete()));
                if (completion.complete()) {
                    if (buf.currChar() != ' ') {
                        buf.write(" ");
                    } else {
                        buf.move(1);
                    }
                }
                if (completion.suffix() != null) {
                    redisplay();
                    Binding op = readBinding(getKeys());
                    if (op != null) {
                        String chars = getString(REMOVE_SUFFIX_CHARS, DEFAULT_REMOVE_SUFFIX_CHARS);
                        String ref = op instanceof Reference ? ((Reference) op).name() : null;
                        if (SELF_INSERT.equals(ref) && chars.indexOf(getLastBinding().charAt(0)) >= 0
                                || ACCEPT_LINE.equals(ref)) {
                            buf.backspace(completion.suffix().length());
                            if (getLastBinding().charAt(0) != ' ') {
                                buf.write(' ');
                            }
                        }
                        pushBackBinding(true);
                    }
                }
                return true;
            }

            List<Candidate> possible = matching.entrySet().stream()
                    .flatMap(e -> e.getValue().stream())
                    .collect(Collectors.toList());

            if (useMenu) {
                buf.move(line.word().length() - line.wordCursor());
                buf.backspace(line.word().length());
                doMenu(possible, line.word(), line::escape);
                return true;
            }

            // Find current word and move to end
            String current;
            if (prefix) {
                current = line.word().substring(0, line.wordCursor());
            } else {
                current = line.word();
                buf.move(line.rawWordLength() - line.rawWordCursor());
            }
            // Now, we need to find the unambiguous completion
            // TODO: need to find common suffix
            String commonPrefix = null;
            for (String key : matching.keySet()) {
                commonPrefix = commonPrefix == null ? key : getCommonStart(commonPrefix, key, caseInsensitive);
            }
            boolean hasUnambiguous = commonPrefix.startsWith(current) && !commonPrefix.equals(current);

            if (hasUnambiguous) {
                buf.backspace(line.rawWordLength());
                buf.write(line.escape(commonPrefix, false));
                callWidget(REDISPLAY);
                current = commonPrefix;
                if ((!isSet(Option.AUTO_LIST) && isSet(Option.AUTO_MENU))
                        || (isSet(Option.AUTO_LIST) && isSet(Option.LIST_AMBIGUOUS))) {
                    if (!nextBindingIsComplete()) {
                        return true;
                    }
                }
            }
            if (isSet(Option.AUTO_LIST)) {
                if (!doList(possible, current, true, line::escape)) {
                    return true;
                }
            }
            if (isSet(Option.AUTO_MENU)) {
                buf.backspace(current.length());
                doMenu(possible, line.word(), line::escape);
            }
            return true;
        } finally {
            size.copy(terminal.getBufferSize());
        }
    }


}
