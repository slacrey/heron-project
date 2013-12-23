package com.loadburn.heron.storage;

import com.loadburn.heron.storage.cache.ICache;
import com.loadburn.heron.storage.config.EntityMetadataConfig;
import com.loadburn.heron.storage.convertion.ResultHandler;
import com.loadburn.heron.storage.sql.Query;
import com.loadburn.heron.storage.transaction.InTransaction;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * @author slacrey (scstlinfeng@yahoo.com)
 *         Date: 13-12-11
 */
public interface EntityStorage {

    public boolean save(Class<?> entityClass, Object entity) throws SQLException;

    public boolean remove(Class<?> entityClass, Object entity) throws SQLException;

    public boolean update(Class<?> entityClass, Object entity, Object primaryKey) throws SQLException;

    public <T> T find(Class<?> entityClass, ResultHandler handler, Object primaryKey) throws SQLException;

    public <T> T find(Class<?> entityClass, ResultHandler handler) throws SQLException;

    public Object getDelegate();

    public void close();

    public boolean isOpen();

    public Query createQuery(Class<?> clazz, String sqlQuery);

    public Query createNamedQuery(Class<?> clazz, String name);

    public InTransaction getTransaction();

    public ICache getCache();

    public Connection getConnection();

    public EntityMetadataConfig getMetadataConfig();

}
