package com.loadburn.heron.storage.convertion.handler;

import com.loadburn.heron.storage.config.EntityMetadata;
import com.loadburn.heron.storage.convertion.ResultSetConverter;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;

/**
 * @author slacrey (scstlinfeng@yahoo.com)
 *         Date: 13-11-5
 */
public class MapListHandler extends AbstractListHandler<Map<String, Object>> {

    @Override
    protected Map<String, Object> handleRow(final EntityMetadata.EntityDescriptor entityDescriptor, ResultSet rs) throws SQLException {
        return ResultSetConverter.delegation(entityDescriptor).toMap(rs);
    }

}
