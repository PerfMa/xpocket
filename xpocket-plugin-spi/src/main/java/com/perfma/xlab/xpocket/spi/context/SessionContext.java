package com.perfma.xlab.xpocket.spi.context;

/**
 *
 * @author gongyu <yin.tong@perfma.com>
 */
public interface SessionContext {

    /**
     * Get current plugin context
     * @return 
     */
    PluginBaseInfo currentPlugin();
    
    /**
     * Get current command context, if exist
     * @return 
     */
    CommandBaseInfo currentCommand();
    
    /**
     * if current plugin attached on a process,here is the id of the process
     * @return 
     */
    int pid();
    
    /**
     * tell XPocket if you attached on a process now
     * @param pid 
     */
    void setPid(int pid);
    
    /**
     * if current plugin connected a network,here is the infomation of the network
     * @return 
     */
    String networkInfo();
    
    /**
     * tell XPocket if you connected a network now
     * @param networkInfo 
     */
    void setNetworkInfo(String networkInfo);
    
    /**
     * set a property
     * @param key
     * @param value 
     */
    void setProp(String key,String value);
    
    /**
     * get a property value
     * @param key 
     * @return  
     */
    String getProp(String key);
    
}
