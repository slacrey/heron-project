package com.loadburn.heron.storage.sql;

import com.loadburn.heron.storage.convertion.ResultHandler;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Map;

/**
 * @author slacrey (scstlinfeng@yahoo.com)
 *         Date: 13-12-12
 */
public interface Query {

    public Connection connection();

    public <T> T list() throws SQLException;

    public int executeUpdate() throws SQLException;

    public Query setMaxResults(int limit);

    public Query setFirstResult(int offset);

    public Query returnAs(ResultHandler<?> handler);

    public Query setParameter(String name, Object value);

    public Query setParameter(int position, Object value);

    public Query setParameterNameMap(Map<String, Object> params);

    public Query setParameterPositionMap(Map<Integer, Object> params);

    public boolean tableExists(String name);

}
