package com.loadburn.heron.storage.convertion.handler;

import com.loadburn.heron.storage.config.EntityMetadata;
import com.loadburn.heron.storage.convertion.ResultHandler;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

/**
 * @author slacrey (scstlinfeng@yahoo.com)
 *         Date: 13-11-5
 */
public abstract class AbstractKeyedHandler<K, V> implements ResultHandler<Map<K, V>> {
    @Override
    public Map<K, V> handle(final EntityMetadata.EntityDescriptor entityDescriptor, ResultSet rs) throws SQLException {
        Map<K, V> result = createMap();
        while (rs.next()) {
            result.put(createKey(entityDescriptor, rs), createRow(entityDescriptor, rs));
        }
        return result;
    }

    protected Map<K, V> createMap() {
        return new HashMap<K, V>();
    }

    protected abstract K createKey(final EntityMetadata.EntityDescriptor entityDescriptor, ResultSet rs) throws SQLException;

    protected abstract V createRow(final EntityMetadata.EntityDescriptor entityDescriptor, ResultSet rs) throws SQLException;
}
