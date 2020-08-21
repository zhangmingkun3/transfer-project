package com.lp.transfer.transferproject.utils;

import java.util.Map;
import java.util.concurrent.*;

/**
 * @Author: zhangmingkun3
 * @Description:
 * @Date: 2020/8/18 14:25
 */
public class ThreadPoolUtils {

    /**
     * key表示设备号建立的数据标识   value 表示接受数据的线程
     */
//    public static Map<String,Thread> shareMap = new ConcurrentHashMap<String, Thread>();

    public static final ThreadPoolExecutor COMMON_POOL = new ThreadPoolExecutor(
            0,
            Integer.MAX_VALUE,
            30,
            TimeUnit.SECONDS,
            new SynchronousQueue<>(),
            new RicStreamThreadFactory("RIC-COMMON"));


    public static ScheduledThreadPoolExecutor buildScheduledPool(int coreSize) {
        return buildScheduledPool("RIC-SCH", coreSize);
    }

    public static ScheduledThreadPoolExecutor buildScheduledPool(String name, int coreSize) {
        return new ScheduledThreadPoolExecutor(coreSize, new RicStreamThreadFactory("RIC-SCH-" + name));
    }


}