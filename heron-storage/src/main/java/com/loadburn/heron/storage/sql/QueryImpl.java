package com.loadburn.heron.storage.sql;

import com.google.common.collect.Maps;
import com.loadburn.heron.storage.cache.CacheUtils;
import com.loadburn.heron.storage.cache.ICache;
import com.loadburn.heron.storage.config.EntityMetadata;
import com.loadburn.heron.storage.convertion.ResultHandler;
import com.loadburn.heron.storage.exceptions.StorageException;

import java.sql.*;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;

/**
 * @author slacrey (scstlinfeng@yahoo.com)
 *         Date: 13-12-12
 */
public abstract class QueryImpl implements Query {

    private final ICache cache;
    private final EntityMetadata.EntityDescriptor entityDescriptor;
    private final Connection connection;

    private volatile boolean supportMetadata = true;
    private ResultHandler<?> handler;
    private Map<String, Object> nameParams = Maps.newHashMap();
    private Map<Integer, Object> positionParams = Maps.newHashMap();

    protected Integer limit;
    protected Integer offset;
    protected String query;

    public QueryImpl(EntityMetadata.EntityDescriptor entityDescriptor, ICache cache, Connection connection, String query) {
        this.connection = connection;
        this.query = query;
        this.entityDescriptor = entityDescriptor;
        this.cache = cache;
    }

    @Override
    public Connection connection() {
        return this.connection;
    }

    @Override
    public Query returnAs(ResultHandler<?> handler) {
        this.handler = handler;
        return this;
    }

    @Override
    public Query setParameter(String name, Object value) {
        this.nameParams.put(name, value);
        return this;
    }

    @Override
    public Query setParameter(int position, Object value) {
        this.positionParams.put(position, value);
        return this;
    }

    @Override
    public Query setParameterNameMap(Map<String, Object> params) {
        this.nameParams = params;
        this.convertToPositionMap(params);
        return this;
    }

    @Override
    public Query setParameterPositionMap(Map<Integer, Object> params) {
        this.positionParams = params;
        return this;
    }

    @Override
    public Query setMaxResults(int limit) {
        this.limit = limit;
        return this;
    }

    @Override
    public Query setFirstResult(int offset) {
        this.offset = offset;
        return this;
    }

    public int executeUpdate() throws SQLException {
        PreparedStatement preparedStatement = null;
        int result = 0;
        try {
            preparedStatement = this.prepareQueryStatement();
            if (entityDescriptor.getEntityCache().cache()) {
                CacheUtils.removeCacheKeyLike(this.cache, entityDescriptor.getEntityCache().name());
            }

            this.fillStatement(preparedStatement, this.positionParams);
            result = preparedStatement.executeUpdate();

        } catch (SQLException e) {
            throwException(e, this.query, this.nameParams);
        } finally {
            try {
                if (preparedStatement != null && !preparedStatement.isClosed())
                    preparedStatement.close();
            } catch (SQLException e) {
                throw new StorageException("ResultSet或PreparedStatement关闭错误", e);
            }
        }
        return result;
    }

    @SuppressWarnings("unchecked")
    public  <T> T list() throws SQLException, StorageException {
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        T result = null;

        try {
            preparedStatement = this.prepareQueryStatement();
            String key = null;
            if (entityDescriptor.getEntityCache().cache()) {
                key = CacheUtils.getCacheKey(entityDescriptor.getEntityCache().name(), this.query, this.nameParams);
                result = (T) CacheUtils.getCacheResult(this.cache, key);
                if (result != null) {
                    return result;
                }
            }

            this.fillStatement(preparedStatement, this.positionParams);
            resultSet = preparedStatement.executeQuery();
            if (resultSet != null) {
                result = (T) this.handler.handle(this.entityDescriptor, resultSet);
                if (entityDescriptor.getEntityCache().cache()) {
                    CacheUtils.putCacheResult(this.cache, key, result);
                    CacheUtils.putCacheStatement(this.cache, entityDescriptor.getEntityCache().name(), key);
                }
            }
        } catch (SQLException e) {
            throwException(e, this.query, this.nameParams);
        } finally {
            if (resultSet != null && !resultSet.isClosed())
                resultSet.close();
            if (preparedStatement != null && !preparedStatement.isClosed())
                preparedStatement.close();
        }

        return result;
    }

    // =========================================================================
    protected abstract PreparedStatement prepareQueryStatement() throws SQLException;

    protected void fillStatement(PreparedStatement stmt, Map<Integer, Object> positionParams) throws SQLException {
        ParameterMetaData pmd = null;
        if (supportMetadata) {
            pmd = stmt.getParameterMetaData();
            int stmtCount = pmd.getParameterCount();
            int paramsCount = positionParams == null ? 0 : positionParams.size();

            if (stmtCount != paramsCount) {
                throw new SQLException("错误的参数数量:  实际的是 "
                        + stmtCount + ", 传入的是 " + paramsCount);
            }
        }

        if (positionParams == null) {
            return;
        }

        for (Map.Entry<Integer, Object> entry : positionParams.entrySet()) {

            Integer key = entry.getKey();
            Object value = entry.getValue();
            if (value != null) {
                stmt.setObject(key, value);
            } else {
                // 很多驱动是不管列类型的
                int sqlType = Types.VARCHAR;
                if (supportMetadata) {
                    try {
                        assert pmd != null;
                        sqlType = pmd.getParameterType(key);
                    } catch (SQLException e) {
                        supportMetadata = false;
                    }
                }
                stmt.setNull(key, sqlType);
            }
        }
    }

    protected void throwException(SQLException cause, String sql, Map<String, Object> params) throws SQLException {
        String causeMessage = cause.getMessage();
        if (causeMessage == null) {
            causeMessage = "";
        }
        StringBuilder msg = new StringBuilder(causeMessage);

        msg.append(" Query: ");
        msg.append(sql);
        msg.append(" Parameters: ");

        if (params == null) {
            msg.append("[]");
        } else {
            for (Map.Entry<String, Object> entry : params.entrySet()) {
                msg.append("[").append(entry.getKey()).append(",").append(entry.getValue()).append("]");
            }
        }

        SQLException e = new SQLException(msg.toString(), cause.getSQLState(),
                cause.getErrorCode());
        e.setNextException(cause);

        throw e;
    }

    /**
     * 转换为位置参数map
     *
     * @param nameParams 名称参数map
     */
    protected void convertToPositionMap(Map<String, Object> nameParams) {

        Matcher matcher = EntityMetadata.NAMED_ARG_PATTERN.matcher(this.query);
        if (nameParams == null) {
            setParameterPositionMap(null);
        }
        boolean find = matcher.find();
        Map<Integer, Object> positionalParams = new HashMap<Integer, Object>();
        int index = 1;
        while (find) {
            Object value = nameParams != null ? nameParams.get(matcher.group().substring(1)) : null;
            if (value == null) {
                throw new IllegalArgumentException("Named parameter map for SQL statement did" +
                        " not contain required parameter: " + matcher.group());
            }
            positionalParams.put(index, value);
            find = matcher.find(matcher.end());
            index++;
        }
        setParameterPositionMap(positionalParams);

        matcher.reset();
        this.query = matcher.replaceAll("?");

    }

    public boolean tableExists(String name) {
        try {
            boolean exists = connection().getMetaData().getTables(null, null, name, null).next();
            if (!exists)
                return connection().getMetaData().getTables(null, null, name.toUpperCase(), null).next();
            else
                return exists;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

}
