package com.loadburn.heron.storage.cache;

import net.sf.ehcache.Cache;
import net.sf.ehcache.CacheManager;
import net.sf.ehcache.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;

/**
 * @author slacrey (scstlinfeng@yahoo.com)
 *         Date: 13-10-31
 */
public class EhCacheStorage implements ICache {

    private final CacheManager cacheManager;
    private final Logger logger = LoggerFactory.getLogger(EhCacheStorage.class.getName());

    public EhCacheStorage(CacheManager cacheManager) {
        this.cacheManager = cacheManager;
    }

    public CacheManager getCacheManager() {
        return cacheManager;
    }

    Cache getOrAddCache(String cacheName) {
        Cache cache = cacheManager.getCache(cacheName);
        if (cache == null) {
            synchronized(cacheManager) {
                cache = cacheManager.getCache(cacheName);
                if (cache == null) {
                    logger.info("Could not executeQuery cache config [" + cacheName + "], using default.");
                    cacheManager.addCacheIfAbsent(cacheName);
                    cache = cacheManager.getCache(cacheName);
                }
            }
        }
        return cache;
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> T get(String cacheName, Object key) {
        Element element = getOrAddCache(cacheName).get(key);
        return element != null ? (T) element.getObjectValue() : null;
    }

    @Override
    public void put(String cacheName, Object key, Object value) {
        getOrAddCache(cacheName).put(new Element(key, value));
    }

    @Override
    public void remove(String cacheName, Object key) {
        getOrAddCache(cacheName).remove(key);
    }

    @Override
    public void removeAll(String cacheName) {
        getOrAddCache(cacheName).removeAll();
    }

    @Override
    public void removeCollection(String cacheName, Collection<?> key) {
        getOrAddCache(cacheName).removeAll(key);
    }
}
