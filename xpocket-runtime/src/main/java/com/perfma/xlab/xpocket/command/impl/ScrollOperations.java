package com.perfma.xlab.xpocket.command.impl;

import com.perfma.xlab.xpocket.command.AbstractSystemCommand;
import com.perfma.xlab.xpocket.scroll.Scroll;
import com.perfma.xlab.xpocket.scroll.ScrollConstants;
import com.perfma.xlab.xpocket.scroll.compile.ScrollCompiler;
import com.perfma.xlab.xpocket.scroll.run.ScrollRunner;
import com.perfma.xlab.xpocket.spi.command.CommandInfo;
import com.perfma.xlab.xpocket.spi.command.XPocketProcessTemplate;
import com.perfma.xlab.xpocket.spi.command.callback.SimpleProcessCallback;
import com.perfma.xlab.xpocket.spi.process.XPocketProcess;
import com.perfma.xlab.xpocket.utils.TerminalUtil;
import com.perfma.xlab.xpocket.utils.XPocketConstants;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author gongyu <yin.tong@perfma.com>
 */
@CommandInfo(name = "scroll", usage = "scroll list \n scroll compile -name scrollname -ns scrollnamespace [-t type Default:groovy] [-output path] [-F file or script] \n scroll exec [-script -t type Default:groovy] [-F file or script]", index = 200)
public class ScrollOperations extends AbstractSystemCommand {

    private static final ScrollCompiler compiler = new ScrollCompiler();
    private static final ScrollRunner runner = new ScrollRunner();

    private static final Map<String, Scroll> scrollMap = new HashMap<>();

    static {
        File dir = new File(XPocketConstants.XPOCKET_SCROLL_PATH);
        File[] files = dir.listFiles(new FilenameFilter() {
            @Override
            public boolean accept(File dir, String name) {
                return name.endsWith("scl");
            }
        });

        if (files != null && files.length > 1) {
            for (File file : files) {
                try (FileInputStream fis = new FileInputStream(file)) {
                    Scroll scroll = runner.parse(fis);
                    scrollMap.put(String.format("%s@%s", scroll.getName(),
                            scroll.getNamespace().toUpperCase()), scroll);
                } catch (Throwable ex) {
                    ex.printStackTrace();
                }
            }
        }
    }

    @Override
    public void invoke(XPocketProcess process) throws Throwable {

        XPocketProcessTemplate.execute(process, new SimpleProcessCallback() {
            @Override
            public String call(String cmd, String[] args) throws Throwable {

                String result = "";

                String subCmd = args[0];
                boolean isScript = false;
                String scriptName = "groovy";
                String sourceFileName = null;
                String targetOutput = XPocketConstants.XPOCKET_SCROLL_PATH;
                String script = null;

                String scrollName = null;
                String scrollNamespace = null;

                for (int i = 1; i < args.length; i++) {
                    String tmp = args[i];
                    switch (tmp) {
                        case "-t":
                            scriptName = args[++i];
                            break;
                        case "-F":
                            sourceFileName = args[++i];
                            break;
                        case "-output":
                            targetOutput = args[++i];
                            break;
                        case "-script":
                            isScript = true;
                            break;
                        case "-name":
                            scrollName = args[++i];
                            break;
                        case "-ns":
                            scrollNamespace = args[++i];
                            break;
                        default:
                            script = tmp;
                    }
                }

                switch (subCmd) {
                    case "list":
                        for (String key : scrollMap.keySet()) {
                            result += key + TerminalUtil.lineSeparator;
                        }
                        break;
                    case "compile":
                        if (script == null && sourceFileName == null) {
                            result = "USAGE : scroll compile -name scrollname -ns scrollnamespace [-t type Default:groovy] [-output path] [-F file] or [script]";
                            break;
                        } else if (script == null && sourceFileName != null) {
                            try (BufferedReader reader = new BufferedReader(
                                    new InputStreamReader(new FileInputStream(sourceFileName)))) {
                                String line;
                                while ((line = reader.readLine()) != null) {
                                    script += line;
                                }
                            }
                        }

                        Scroll scroll = new Scroll();
                        scroll.setName(scrollName);
                        scroll.setNamespace(scrollNamespace);
                        scroll.setScriptName(scriptName);
                        scroll.setVersion(ScrollConstants.VERSION);

                        scroll.setScripts(compiler.compile(scriptName, script));

                        String outputFile = String.format("%s%s%s_%s.scl", targetOutput, File.separator, scrollName, scrollNamespace);
                        try (FileOutputStream output = new FileOutputStream(outputFile)) {
                            compiler.compile(scroll, output);
                        }

                        scrollMap.put(String.format("%s@%s", scroll.getName(), scroll.getNamespace().toUpperCase()), scroll);
                        result = String.format("Compile complete! Output file is %s.Loaded scroll is %s@%s",
                                outputFile, scroll.getName(), scroll.getNamespace().toUpperCase());
                        break;
                    case "exec":

                        if (isScript) {
                            if (script == null && sourceFileName == null) {
                                result = "USAGE : scroll exec [-script -t type Default:groovy] [-F file or script]";
                                break;
                            } else if (script == null && sourceFileName != null) {
                                try (BufferedReader reader = new BufferedReader(
                                        new InputStreamReader(new FileInputStream(sourceFileName)))) {
                                    String line;
                                    script = "";
                                    while ((line = reader.readLine()) != null) {
                                        script += line;
                                    }
                                }
                            }

                            runner.run(scriptName, script);
                        } else if (sourceFileName != null) {

                            try (FileInputStream input = new FileInputStream(sourceFileName)) {
                                Scroll scrollForRunner = runner.parse(input);
                                runner.run(scrollForRunner);
                            }
                        } else {
                            if (script == null) {
                                result = "USAGE : scroll exec [-script -t type Default:groovy] [-F file or script or scrollName@scrollNameSpace]";
                                break;
                            }

                            runner.run(scrollMap.get(script));
                        }
                        break;
                    default:
                        result = String.format("Undefined command %s .", subCmd);
                }

                return result;
            }
        });

    }

}
