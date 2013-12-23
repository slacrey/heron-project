package com.loadburn.heron.storage.convertion.handler;

import com.loadburn.heron.storage.config.EntityMetadata;
import com.loadburn.heron.storage.convertion.ResultHandler;
import com.loadburn.heron.storage.convertion.ResultSetConverter;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * @author slacrey (scstlinfeng@yahoo.com)
 *         Date: 13-11-5
 */
public class ArrayListHandler implements ResultHandler<Object[]> {

    @Override
    public Object[] handle(final EntityMetadata.EntityDescriptor entityDescriptor, ResultSet rs) throws SQLException {

        return ResultSetConverter.delegation(entityDescriptor).toArray(rs);
    }

}
