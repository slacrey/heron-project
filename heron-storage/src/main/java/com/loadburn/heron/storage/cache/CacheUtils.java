package com.loadburn.heron.storage.cache;

import com.google.common.collect.Lists;
import com.loadburn.heron.storage.config.EntityMetadata;
import com.loadburn.heron.utils.StringUtils;
import net.sf.ehcache.search.Query;
import net.sf.ehcache.search.Result;

import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * @author slacrey (scstlinfeng@yahoo.com)
 *         Date: 13-12-14
 */
public class CacheUtils {

    private final static Object cacheLock = new Object();
    private final static String REPLACE_CHAR = "_";
    private final static String CACHE_NAME_SPLIT_CHAR = ".";
    private final static String ARGUMENT_SPLIT_CHAR = "=";
    private final static String ARGUMENTS_SPLIT_CHAR = ",";

    /**
     * 生成缓存Key
     *
     * @param cacheName 实体类@Cache配置的缓存名称
     * @param statement 查询实体类的查询语句
     * @param arguments 查询语句的参数
     * @return 返回缓存Key字符串
     */
    public static String getCacheKey(String cacheName, String statement, Map<String, Object> arguments) {

        StringBuilder keyBuilder = new StringBuilder();
        String key = StringUtils.replaceAs(statement, REPLACE_CHAR);
        keyBuilder.append(cacheName).append(CACHE_NAME_SPLIT_CHAR).append(key);
        if (arguments != null) {
            keyBuilder.append(CACHE_NAME_SPLIT_CHAR);
            for (Map.Entry<String, Object> entry : arguments.entrySet()) {
                keyBuilder.append(entry.getKey()).append(ARGUMENT_SPLIT_CHAR).append(entry.getValue()).append(ARGUMENTS_SPLIT_CHAR);
            }
        }
        return keyBuilder.toString();

    }

    public static void removeCacheKeyLike(ICache cache, String cacheName) {

        List<String> keyList = cache.get(EntityMetadata.STORAGE_CACHE_NAME, cacheName);
        if (keyList != null) {
            cache.removeCollection(EntityMetadata.STORAGE_CACHE_NAME, keyList);
            keyList.clear();
        }

    }

    /**
     * 获取缓存对象
     *
     * @param cache 缓存对象
     * @return 返回指定对象
     */
    public static Object getCacheResult(ICache cache, String key) {
        return cache.get(EntityMetadata.STORAGE_CACHE_NAME, key);
    }

    public static void putCacheResult(ICache cache, String key, Object value) {
        cache.put(EntityMetadata.STORAGE_CACHE_NAME, key, value);
    }

    public static void putCacheStatement(ICache cache, String cacheName, String key) {

        synchronized(cacheLock){
            List<String> cacheKeyList = cache.get(EntityMetadata.STORAGE_CACHE_NAME, cacheName);
            if (cacheKeyList == null) {
                cacheKeyList = Lists.newArrayList();
                cacheKeyList.add(key);
            } else {
                if (!cacheKeyList.contains(key)) {
                    cacheKeyList.add(key);
                }
            }
            cache.put(EntityMetadata.STORAGE_CACHE_NAME, cacheName, cacheKeyList);
        }

    }

}
