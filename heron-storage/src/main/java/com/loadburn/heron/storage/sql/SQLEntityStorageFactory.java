package com.loadburn.heron.storage.sql;

import com.jolbox.bonecp.BoneCP;
import com.loadburn.heron.storage.EntityStorage;
import com.loadburn.heron.storage.EntityStorageFactory;
import com.loadburn.heron.storage.cache.EhCacheStorage;
import com.loadburn.heron.storage.config.EntityMetadata;
import com.loadburn.heron.storage.config.EntityMetadataConfig;
import com.loadburn.heron.storage.config.StorageDataSource;
import com.loadburn.heron.storage.exceptions.StorageException;
import com.loadburn.heron.storage.sql.mysql.MySQLEntityStorage;
import net.sf.ehcache.CacheManager;

import java.io.Serializable;
import java.sql.SQLException;

/**
 * @author slacrey (scstlinfeng@yahoo.com)
 *         Date: 13-12-12
 */
public abstract class SQLEntityStorageFactory implements EntityStorageFactory, Serializable {

    private final EntityMetadata entityMetadata;
    private final CacheManager cacheManager;
    private StorageDataSource dataSource;
    private BoneCP pool;

    /**
     * 初始化数据源
     *
     * @throws java.io.IOException
     */
    private void initDataPool() {
        try {
            this.pool = new BoneCP(dataSource);
        } catch (SQLException e) {
            throw new StorageException("数据库连接池初始化错误", e);
        }
    }

    public SQLEntityStorageFactory(StorageDataSource dataSource, EntityMetadata entityMetadata, CacheManager cacheManager) {
        this.entityMetadata = entityMetadata;
        this.dataSource = dataSource;
        this.cacheManager = cacheManager;
        initDataPool();
    }

    @Override
    public EntityStorage createEntityStorage() {
        if (getPool() == null) {
              throw new StorageException("数据库链接池为空");
        }
        try {
            return new MySQLEntityStorage(new EntityMetadataConfig(entityMetadata), new EhCacheStorage(cacheManager), pool.getConnection());
        } catch (SQLException e) {
            throw new StorageException("数据库连接池获取链接失败", e);
        }
    }

    @Override
    public void close() {
        if (pool != null) {
            pool.shutdown();
        }
        if (cacheManager != null) {
            cacheManager.shutdown();
        }
    }

    @Override
    public boolean isOpen() {
        return pool != null;
    }

    public BoneCP getPool() {
        return pool;
    }

    public EntityMetadata getEntityMetadata() {
        return entityMetadata;
    }

    public CacheManager getCacheManager() {
        return cacheManager;
    }

    public StorageDataSource getDataSource() {
        return dataSource;
    }
}
