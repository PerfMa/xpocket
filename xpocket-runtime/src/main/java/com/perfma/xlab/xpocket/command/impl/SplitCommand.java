package com.perfma.xlab.xpocket.command.impl;

import com.perfma.xlab.xpocket.spi.command.CommandInfo;
import com.perfma.xlab.xpocket.spi.process.XPocketProcess;

/**
 * @author xinxian
 */
@CommandInfo(name = "split", usage = "split -f [separator] -i [index] -l [limit] input or split -f [separator] -i [index] -l [limit] if it's a pipe operation", index = 10)
public class SplitCommand extends AbstractSystemCommand {

    @Override
    public boolean isPiped() {
        return true;
    }

    @Override
    public void invoke(XPocketProcess process) throws Throwable {
        String input = process.input();
        String[] args = process.getArgs() == null
                ? new String[0]
                : process.getArgs();

        boolean isPressSpace = true;
        String separator = " ";
        int index = 0;
        int limit = -1;
        try {
            for (int i = 0; i < args.length; i++) {
                String arg = args[i];
                switch (arg) {
                    case "-f":
                        i++;
                        separator = args[i];
                        break;
                    case "-o":
                        isPressSpace = false;
                        break;
                    case "-i":
                        i++;
                        index = Integer.parseInt(args[i]);
                        break;
                    case "-l":
                        i++;
                        limit = Integer.parseInt(args[i]);
                        break;
                    default:
                        if (input != null) {
                            input = arg;
                        }
                }
            }
        } catch (NumberFormatException ex) {
            process.output("WARN : some numberic args is not in correct format,use default values: index=0,limit=-1 \n");
        } catch (Throwable ex) {
            process.output("ERROR : some unhandled error is catched,msg : " + ex.getMessage());
            process.end();
            return;
        }

        if (input == null) {
            process.end();
        }

        input = input.trim();
        if (isPressSpace) {
            input = pressSpace(input);
        }

        String[] result = input.split(separator, limit);
        for(String s : result){
            process.getExecuteContext().addInternalVar(s);
        }
        if (index >= 0 && result.length > index) {
            process.output(result[index]);
        }

        process.end();
    }

    private String pressSpace(String input) {
        char[] charArray = input.toCharArray();
        int cursor = 0;
        boolean spaceflag = false;
        for (int i = 0; i < charArray.length; i++) {
            char c = charArray[i];
            switch (c) {
                case ' ':
                    spaceflag = true;
                    break;
                default:
                    if (spaceflag) {
                        charArray[cursor++] = ' ';
                        spaceflag = false;
                    }
                    charArray[cursor++] = c;
            }
        }

        return new String(charArray, 0, cursor);
    }

}
