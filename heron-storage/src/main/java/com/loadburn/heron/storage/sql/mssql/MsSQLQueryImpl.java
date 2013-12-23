package com.loadburn.heron.storage.sql.mssql;

import com.loadburn.heron.storage.cache.ICache;
import com.loadburn.heron.storage.config.EntityMetadata;
import com.loadburn.heron.storage.sql.QueryImpl;
import com.loadburn.heron.storage.sql.QueryUtils;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.regex.Pattern;

/**
 * @author slacrey (scstlinfeng@yahoo.com)
 *         Date: 13-12-16
 */
public class MsSQLQueryImpl extends QueryImpl {

    public MsSQLQueryImpl(EntityMetadata.EntityDescriptor entityDescriptor, ICache cache, Connection connection,
                          String query) {
        super(entityDescriptor, cache, connection, query);
    }

    @Override
    protected PreparedStatement prepareQueryStatement() throws SQLException {

        String realQuery = QueryUtils.replaceFirstStatement(this.query, "select", "");

        StringBuilder queryBuilder = new StringBuilder("select * from ( ")
                .append("select row_number() over(order by tempcolumn) temprownumber,* ");

        if (this.limit != null && this.offset != null) {
            queryBuilder.append("from (select top ").append(offset + limit).append("tempcolumn=0,")
                    .append(realQuery).append(") t")
                    .append(") tt where temprownumber > ").append(offset);
        } else if (this.limit != null) {
            queryBuilder.append("from (select top ").append(limit).append("tempcolumn=0,")
                    .append(realQuery).append(") t")
                    .append(") tt where temprownumber > ").append(0);
        }

        return connection().prepareStatement(queryBuilder.toString());
    }
}
