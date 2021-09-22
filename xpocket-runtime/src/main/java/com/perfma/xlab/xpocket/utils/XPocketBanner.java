package com.perfma.xlab.xpocket.utils;

import java.io.FileInputStream;
import java.nio.charset.Charset;
import java.util.Collections;
import java.util.Map;
import org.apache.commons.io.IOUtils;



public class XPocketBanner {
 
    private static String LOGO_LOCATION = System.getProperty("XPOCKET_CONFIG_PATH") + "logo.txt";

    private static String LOGO = "";
    
    /* XPocket logo 颜色顺序 */
    private static final String[] COLORS = {"red","magenta","yellow","green","magenta","yellow","cyan"};

    static {
        try {
            String logoText = IOUtils.toString(new FileInputStream(LOGO_LOCATION),Charset.defaultCharset());
            logoText = logoText.replaceAll("\r\n", "\n");
            String[] elements = logoText.split("\n");
            int /*高度*/h = 5,/*字符数*/c = 7,/*宽度*/w = 8;
            StringBuilder logoBuilder = new StringBuilder(logoText.length());
            
            for(int i=0;i<h;i++) {
                for(int j=0;j<7;j++) {
                    String line = elements[j * h + i];
                    logoBuilder.append("@|")
                            .append(COLORS[j])
                            .append(StringUtils.fillWithSpace(line, w))
                            .append("|@");
                } 
                logoBuilder.append(TerminalUtil.lineSeparator);
            }
            
            LOGO = logoBuilder.substring(0, logoBuilder.length());
            
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }


    public static String logo() {
        return LOGO;
    }

    public static String welcome() {
        return welcome(Collections.<String, String>emptyMap());
    }

    public static String welcome(Map<String, String> infos) {
        return logo();
    }
}
