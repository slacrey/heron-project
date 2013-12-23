package com.loadburn.heron.storage.sql.mysql;

import com.loadburn.heron.storage.cache.ICache;
import com.loadburn.heron.storage.config.EntityMetadata;
import com.loadburn.heron.storage.sql.QueryImpl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * @author slacrey (scstlinfeng@yahoo.com)
 *         Date: 13-12-12
 */
public class MySQLQueryImpl extends QueryImpl {

    public MySQLQueryImpl(final EntityMetadata.EntityDescriptor entityDescriptor, final ICache cache,
                          final Connection connection, String query) {
        super(entityDescriptor, cache, connection, query);
    }

    @Override
    protected PreparedStatement prepareQueryStatement() throws SQLException {
        if (this.limit != null && this.offset != null) {
            this.query += " limit " + offset + "," + limit;
        } else if (this.limit != null) {
            this.query += " limit 0," + limit;
        }
        return connection().prepareStatement(this.query);
    }

}
