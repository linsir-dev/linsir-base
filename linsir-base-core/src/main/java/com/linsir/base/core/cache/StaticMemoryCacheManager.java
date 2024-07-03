package com.linsir.base.core.cache;

import org.springframework.cache.Cache;
import org.springframework.cache.concurrent.ConcurrentMapCache;

import java.util.ArrayList;
import java.util.List;

/**
 * @author linsir
 * @title: StaticMemoryCacheManager
 * @projectName linsir
 * @description: 静态不变化的数据内存缓存manager
 * @date 2022/3/20 0:39
 */
public class StaticMemoryCacheManager extends BaseCacheManager{

    public StaticMemoryCacheManager(String... cacheNames){
        List<Cache> caches = new ArrayList<>();
        for(String cacheName : cacheNames){
            caches.add(new ConcurrentMapCache(cacheName));
        }
        setCaches(caches);
        super.afterPropertiesSet();
    }
}
