package com.perfma.xlab.xpocket.scroll;

import java.util.List;
import java.util.Properties;

/**
 *
 * @author gongyu <yin.tong@perfma.com>
 */
public class Scroll {

    private long version;
    private String name;
    private String namespace;
    private Properties info = new Properties();
    
    private String scriptName;
    private Properties scriptHeader = new Properties();
    private List<byte[]> scripts;

    public long getVersion() {
        return version;
    }

    public void setVersion(long version) {
        this.version = version;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getNamespace() {
        return namespace;
    }

    public void setNamespace(String namespace) {
        this.namespace = namespace;
    }

    public Properties getInfo() {
        return info;
    }

    public void setInfo(Properties info) {
        this.info = info;
    }

    public String getScriptName() {
        return scriptName;
    }

    public void setScriptName(String scriptName) {
        this.scriptName = scriptName;
    }

    public Properties getScriptHeader() {
        return scriptHeader;
    }

    public void setScriptHeader(Properties scriptHeader) {
        this.scriptHeader = scriptHeader;
    }

    public List<byte[]> getScripts() {
        return scripts;
    }

    public void setScripts(List<byte[]> scripts) {
        this.scripts = scripts;
    } 
    
}
