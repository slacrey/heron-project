package com.loadburn.heron.storage.convertion;

import com.loadburn.heron.storage.config.EntityMetadata;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * @author slacrey (scstlinfeng@yahoo.com)
 *         Date: 13-11-1
 */
public interface ResultHandler<T> {

    T handle(final EntityMetadata.EntityDescriptor entityDescriptor, ResultSet rs) throws SQLException;

}
