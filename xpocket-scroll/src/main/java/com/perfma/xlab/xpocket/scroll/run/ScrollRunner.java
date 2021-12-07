package com.perfma.xlab.xpocket.scroll.run;

import com.perfma.xlab.xpocket.scroll.Scroll;
import com.perfma.xlab.xpocket.scroll.ScrollConstants;
import com.perfma.xlab.xpocket.scroll.exception.ScrollParseException;
import com.perfma.xlab.xpocket.scroll.utils.BytesUtils;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.ServiceLoader;
import java.util.concurrent.ConcurrentHashMap;

/**
 *
 * @author gongyu <yin.tong@perfma.com>
 */
public class ScrollRunner {

    private static final Map<String, ScrollScriptRunner> runners = new ConcurrentHashMap<>();

    static {
        Iterator<ScrollScriptRunner> runnersIt = ServiceLoader.load(ScrollScriptRunner.class).iterator();
        while (runnersIt.hasNext()) {
            ScrollScriptRunner runner = runnersIt.next();
            runners.putIfAbsent(runner.name(), runner);
        }
    }

    public String run(String scriptName,String script) {
        String result = "OK";
        ScrollScriptRunner runner = runners.get(scriptName.toUpperCase());
        runner.run(script);
        return result;
    }

    public Scroll parse(InputStream inputStream) throws ScrollParseException {
        Scroll scroll = new Scroll();
        try (DataInputStream reader = new DataInputStream(inputStream)) {
            byte[] buffer = new byte[1024];
            reader.read(buffer, 0, 8);

            if (!BytesUtils.isEquals(buffer, ScrollConstants.MAGIC_CODE, 0, 6)) {
                throw new ScrollParseException("PARSE EXCEPTION : wrong magic code : " + new String(buffer, 0, 6));
            } else {
                scroll.setVersion(reader.readLong());

                int nameLength = (int) reader.readLong();
                reader.read(buffer, 0, nameLength);
                scroll.setName(new String(buffer, 0, nameLength));

                int nameSpaceLength = (int) reader.readLong();
                reader.read(buffer, 0, nameSpaceLength);
                scroll.setNamespace(new String(buffer, 0, nameSpaceLength));

                reader.skipBytes(BytesUtils.paddingSize(8 + 8 + nameLength + nameSpaceLength));

                int validateLen = (int) reader.readLong();
                reader.skipBytes(validateLen + BytesUtils.paddingSize(validateLen));

                int infoLen = (int) reader.readLong();
                reader.skipBytes(infoLen + BytesUtils.paddingSize(infoLen));

                int scriptTypeLen = (int) reader.readLong();
                reader.read(buffer, 0, scriptTypeLen + BytesUtils.paddingSize(scriptTypeLen));

                scroll.setScriptName(new String(buffer, 0, scriptTypeLen));

                int contentHeaderLen = (int) reader.readLong();
                reader.skipBytes(contentHeaderLen);

                int contentLen = (int) reader.readLong();

                List<byte[]> scripts = new ArrayList<>();
                int temp = contentLen;
                while (temp > 0) {
                    int scriptLen = (int) reader.readLong();
                    byte[] script = new byte[scriptLen];
                    scripts.add(script);
                    reader.read(script);
                    temp -= scriptLen + 8;
                }

                reader.skipBytes(BytesUtils.paddingSize(contentLen));
                scroll.setScripts(scripts);
            }

        } catch (IOException ex) {
            ex.printStackTrace();
        }

        return scroll;
    }

    public String run(Scroll scroll) {
        String result = "OK";
        try {
            ScrollScriptRunner runner = runners.get(scroll.getScriptName().toUpperCase());
            runner.run(scroll.getScripts());
        } catch (Throwable ex) {
            ex.printStackTrace();
        }

        return result;

    }

    public static void main(String[] args) throws FileNotFoundException, ScrollParseException {
        ScrollRunner runner = new ScrollRunner();
        Scroll scroll = runner.parse(new FileInputStream("test.scl"));
        runner.run(scroll);

    }

}
