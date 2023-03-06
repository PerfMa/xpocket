package com.perfma.xlab.xpocket.framework.spi.impl.mxbean;

import com.perfma.xlab.xpocket.utils.XPocketCommandHelper;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.locks.LockSupport;
import javax.management.Attribute;
import javax.management.AttributeList;
import javax.management.AttributeNotFoundException;
import javax.management.DynamicMBean;
import javax.management.InvalidAttributeValueException;
import javax.management.MBeanAttributeInfo;
import javax.management.MBeanConstructorInfo;
import javax.management.MBeanException;
import javax.management.MBeanInfo;
import javax.management.MBeanNotificationInfo;
import javax.management.MBeanOperationInfo;
import javax.management.ReflectionException;

/**
 *
 * @author gongyu <tongyin.ty@perfma.com>
 */
public class XPocketMXBeanServiceImpl implements DynamicMBean {

    private static MBeanInfo info;

    public AtomicLong lastCall = new AtomicLong(System.currentTimeMillis()); 
    
    public AtomicBoolean isRunning = new AtomicBoolean(false);

    @Override
    public Object getAttribute(String attribute) throws AttributeNotFoundException, MBeanException, ReflectionException {
        return "";
    }

    @Override
    public void setAttribute(Attribute attribute) throws AttributeNotFoundException, InvalidAttributeValueException, MBeanException, ReflectionException {

    }

    @Override
    public AttributeList getAttributes(String[] attributes) {
        return null;
    }

    @Override
    public AttributeList setAttributes(AttributeList attributes) {
        return null;
    }

    @Override
    public Object invoke(String actionName, Object[] params, String[] signature) throws MBeanException, ReflectionException {
        try {
            this.isRunning.set(true);
            switch (actionName) {
                case "invoke":
                    return invoke((String) params[0]);
                case "stop":
                    stop();
                    return "OK";
            }

            return String.format("UNDEFINED OPERATION : %s", actionName);
        } finally {
            this.lastCall.set(System.currentTimeMillis());
            this.isRunning.set(false);
        }
    }

    @Override
    public MBeanInfo getMBeanInfo() {
        return info;
    }

    public String invoke(String command) {
        return XPocketCommandHelper.execByResult(command);
    }

    public void stop() {
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                LockSupport.parkNanos(TimeUnit.NANOSECONDS.convert(1, TimeUnit.SECONDS));
                System.exit(0);
            }
        }, "SHUT-DOWN");
        t.start();
    }

    static {
        try {
            MBeanOperationInfo operation0 = new MBeanOperationInfo("invoke XPocket command with mxbean.", XPocketMXBeanServiceImpl.class.getDeclaredMethod("invoke", String.class));
            MBeanOperationInfo operation1 = new MBeanOperationInfo("stop XPocket.", XPocketMXBeanServiceImpl.class.getDeclaredMethod("stop"));
            info = new MBeanInfo(XPocketMXBeanServiceImpl.class.getName(), "",
                    new MBeanAttributeInfo[0], new MBeanConstructorInfo[0],
                    new MBeanOperationInfo[]{operation0, operation1}, new MBeanNotificationInfo[0]);
        } catch (Throwable ex) {
            ex.printStackTrace();
        }
    }

}
