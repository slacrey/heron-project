package com.loadburn.heron.storage.sql.mssql;

import com.loadburn.heron.storage.EntityStorage;
import com.loadburn.heron.storage.cache.EhCacheStorage;
import com.loadburn.heron.storage.config.EntityMetadata;
import com.loadburn.heron.storage.config.EntityMetadataConfig;
import com.loadburn.heron.storage.config.StorageDataSource;
import com.loadburn.heron.storage.exceptions.StorageException;
import com.loadburn.heron.storage.sql.SQLEntityStorageFactory;
import net.sf.ehcache.CacheManager;

import java.sql.SQLException;

/**
 * @author slacrey (scstlinfeng@yahoo.com)
 *         Date: 13-12-12
 */
public class MsSQLEntityStorageFactory extends SQLEntityStorageFactory {

    public MsSQLEntityStorageFactory(StorageDataSource dataSource, EntityMetadata entityMetadata, CacheManager cacheManager) {
        super(dataSource, entityMetadata, cacheManager);
    }

    @Override
    public EntityStorage createEntityStorage() {
        if (getPool() == null) {
            throw new StorageException("数据库链接池为空");
        }
        try {
            return new MsSQLEntityStorage(new EntityMetadataConfig(getEntityMetadata()),
                    new EhCacheStorage(getCacheManager()), getPool().getConnection());
        } catch (SQLException e) {
            throw new StorageException("数据库连接池获取链接失败", e);
        }
    }

}
