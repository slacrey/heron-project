package com.loadburn.heron.storage.convertion.handler;

import com.loadburn.heron.storage.config.EntityMetadata;
import com.loadburn.heron.storage.convertion.ResultSetConverter;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * @author slacrey (scstlinfeng@yahoo.com)
 *         Date: 13-11-5
 */
public class BeanMapHandler<K, V> extends AbstractKeyedHandler<K, V> {

    private final Class<V> type;

    private final int columnIndex;

    private final String columnName;

    public BeanMapHandler(Class<V> type) {
        this(type, 1, null);
    }

    public BeanMapHandler(Class<V> type, String columnName) {
        this(type, 1, columnName);
    }

    private BeanMapHandler(Class<V> type,
                           int columnIndex, String columnName) {
        super();
        this.type = type;
        this.columnIndex = columnIndex;
        this.columnName = columnName;
    }

    @Override
    protected K createKey(final EntityMetadata.EntityDescriptor entityDescriptor, ResultSet rs) throws SQLException {
        return (columnName == null) ?
                (K) rs.getObject(columnIndex) :
                (K) rs.getObject(columnName);
    }

    @Override
    protected V createRow(final EntityMetadata.EntityDescriptor entityDescriptor, ResultSet rs) throws SQLException {
        return ResultSetConverter.delegation(entityDescriptor).toBean(rs, type);
    }
}
