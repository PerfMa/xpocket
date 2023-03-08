package com.perfma.xlab.xpocket.launcher;

import com.perfma.xlab.xpocket.plugin.manager.MainUIManager;
import com.perfma.xlab.xpocket.plugin.util.Constants;
import com.perfma.xlab.xpocket.utils.XPocketConstants;

import java.util.ArrayList;
import java.util.List;

/**
 * @author gongyu <yin.tong@perfma.com>
 */
public class XPocketLauncher {

    public static void main(String[] args) {
        XPocketConstants.localInit();

        List<String> argsForCore = new ArrayList<>();
        
        if(System.getProperty(Constants.RUN_MODE_KEY) == null) {
            System.setProperty(Constants.RUN_MODE_KEY,
                    Constants.DEFAULT_RUN_MODE);
        }
        
        if(args != null && args.length >= 2) {
            for(int i=0;i<args.length;i++) {
                String arg = args[i];
                if("-run_mode".equals(arg)) {
                    i++;
                    System.setProperty(Constants.RUN_MODE_KEY,
                            args[i].toUpperCase());
                } else if("-simple".equals(arg)) {
                    System.setProperty(Constants.XPOCKET_SIMPLE_MODE,
                            "true");
                } else {
                    argsForCore.add(arg);
                }
            }
        }
        
        String[] argArrayForCore =  new String[argsForCore.size()];
        argsForCore.toArray(argArrayForCore);
        
        MainUIManager.start(new String[]{"META-INF/xpocket.def"}, argArrayForCore,null);
    }

}
