package com.loadburn.heron.storage.cache;

import com.google.inject.ImplementedBy;
import net.sf.ehcache.CacheManager;

import java.util.Collection;

/**
 * @author slacrey (scstlinfeng@yahoo.com)
 *         Date: 13-10-31
 */
public interface ICache {

    CacheManager getCacheManager();

    <T> T get(String cacheName, Object key);

    void put(String cacheName, Object key, Object value);

    void remove(String cacheName, Object key);

    void removeAll(String cacheName);

    void removeCollection(String cacheName, Collection<?> key);
}
