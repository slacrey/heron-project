package com.loadburn.heron.storage.sql.mssql;

import com.loadburn.heron.storage.cache.ICache;
import com.loadburn.heron.storage.config.EntityMetadata;
import com.loadburn.heron.storage.config.EntityMetadataConfig;
import com.loadburn.heron.storage.sql.Query;
import com.loadburn.heron.storage.sql.SQLEntityStorage;

import java.sql.Connection;

/**
 * @author slacrey (scstlinfeng@yahoo.com)
 *         Date: 13-12-12
 */
public class MsSQLEntityStorage extends SQLEntityStorage {

    public MsSQLEntityStorage(EntityMetadataConfig metadataConfig, ICache cache, Connection connection) {
        super(metadataConfig, cache, connection);
    }

    @Override
    public Query returnQuery(EntityMetadata.EntityDescriptor entityDescriptor, ICache cache,
                             Connection connection, String statement) {
        return new MsSQLQueryImpl(entityDescriptor, cache, connection, statement);
    }

}
