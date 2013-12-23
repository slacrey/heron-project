package com.loadburn.heron.storage.convertion.handler;

import com.loadburn.heron.storage.config.EntityMetadata;
import com.loadburn.heron.storage.convertion.ResultHandler;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author slacrey (scstlinfeng@yahoo.com)
 *         Date: 13-11-5
 */
public abstract class AbstractListHandler<T> implements ResultHandler<List<T>> {

    @Override
    public List<T> handle(final EntityMetadata.EntityDescriptor entityDescriptor, ResultSet rs) throws SQLException {
        List<T> rows = new ArrayList<T>();
        while (rs.next()) {
            rows.add(this.handleRow(entityDescriptor, rs));
        }
        return rows;
    }

    protected abstract T handleRow(final EntityMetadata.EntityDescriptor entityDescriptor, ResultSet rs) throws SQLException;

}
