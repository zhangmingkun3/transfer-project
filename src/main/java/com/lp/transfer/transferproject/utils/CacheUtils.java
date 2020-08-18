package com.lp.transfer.transferproject.utils;

import com.github.benmanes.caffeine.cache.CacheLoader;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.LoadingCache;
import org.springframework.boot.autoconfigure.cache.CacheProperties;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * @Author: zhangmingkun3
 * @Description:
 * @Date: 2020/8/18 16:17
 */
public class CacheUtils {

    /**
     * expireAfterAccess  基于时间(Time-based)  代表着最后一次访问之后多久过期
     *
     */
    public static LoadingCache<String, List<String>> localCache = Caffeine.newBuilder().initialCapacity(1)
            .maximumSize(100).expireAfterWrite(1, TimeUnit.MINUTES)
            .build(new CacheLoader<String, List<String>>() {
                //默认的数据加载实现，当调用get取值的时候，如果key没有对应的值，就调用这个方法进行加载
                @Override
                public List<String> load(String key)  {
                    return new ArrayList<>();
                }
            });

}