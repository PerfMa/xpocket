package com.perfma.xlab.xpocket.launcher;

import com.perfma.xlab.xpocket.plugin.manager.MainUIManager;

/**
 * @author gongyu <yin.tong@perfma.com>
 */
public class XPocketLauncher {

    public static void main(String[] args) {
        MainUIManager.start("META-INF/xpocket.def", args);
    }

}
