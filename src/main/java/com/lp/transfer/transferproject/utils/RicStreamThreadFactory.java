package com.lp.transfer.transferproject.utils;

import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @Author: zhangmingkun3
 * @Description:
 * @Date: 2020/8/18 14:27
 */
public class RicStreamThreadFactory implements ThreadFactory {

    private final ThreadGroup group;

    private final AtomicInteger threadNumber = new AtomicInteger(1);

    private final String namePrefix;

    public RicStreamThreadFactory() {
        SecurityManager s = System.getSecurityManager();
        group = (s != null) ? s.getThreadGroup() : Thread.currentThread().getThreadGroup();
        namePrefix = "RIC-%d";
    }

    public RicStreamThreadFactory(String prefix) {
        SecurityManager s = System.getSecurityManager();
        group = (s != null) ? s.getThreadGroup() : Thread.currentThread().getThreadGroup();
        namePrefix = prefix + "-%d" ;
    }

    @Override
    public Thread newThread(Runnable r) {
        Thread t = new Thread(group, r,
                String.format(namePrefix,  threadNumber.getAndIncrement()),
                0);
        if (t.isDaemon()) {
            t.setDaemon(false);
        }
        if (t.getPriority() != Thread.NORM_PRIORITY) {
            t.setPriority(Thread.NORM_PRIORITY);
        }
        return t;
    }

}
