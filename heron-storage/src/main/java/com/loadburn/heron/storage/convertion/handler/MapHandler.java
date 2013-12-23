package com.loadburn.heron.storage.convertion.handler;

import com.loadburn.heron.storage.config.EntityMetadata;
import com.loadburn.heron.storage.convertion.ResultHandler;
import com.loadburn.heron.storage.convertion.ResultSetConverter;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;

/**
 * @author slacrey (scstlinfeng@yahoo.com)
 *         Date: 13-11-5
 */
public class MapHandler implements ResultHandler<Map<String, Object>> {
    @Override
    public Map<String, Object> handle(final EntityMetadata.EntityDescriptor entityDescriptor, ResultSet rs) throws SQLException {
        return rs.next() ? ResultSetConverter.delegation(entityDescriptor).toMap(rs) : null;
    }
}
