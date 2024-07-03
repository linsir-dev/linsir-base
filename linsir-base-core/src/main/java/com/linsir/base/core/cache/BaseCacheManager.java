package com.linsir.base.core.cache;

import org.springframework.cache.Cache;
import org.springframework.cache.concurrent.ConcurrentMapCache;
import org.springframework.cache.support.SimpleCacheManager;

/**
 * @author linsir
 * @title: BaseCacheManager
 * @projectName linsir
 * @description: 缓存manager父类
 * @date 2022/3/20 0:22
 */
public abstract class BaseCacheManager extends SimpleCacheManager {

    /**
     * 获取缓存对象
     * @param objKey
     * @param <T>
     * @return
     */
    public <T> T getCacheObj(String cacheName, Object objKey, Class<T> tClass){
        Cache cache = getCache(cacheName);
        return cache != null? cache.get(objKey, tClass) : null;
    }

    /**
     * 缓存对象
     * @param cacheName
     * @param objKey
     * @param obj
     */
    public void putCacheObj(String cacheName, Object objKey, Object obj){
        Cache cache = getCache(cacheName);
        cache.put(objKey, obj);
    }

    /**
     * 尚未初始化的
     * @param cacheName
     * @return
     */
    public boolean isUninitializedCache(String cacheName){
        ConcurrentMapCache cache = (ConcurrentMapCache)getCache(cacheName);
        return cache.getNativeCache().isEmpty();
    }

}
