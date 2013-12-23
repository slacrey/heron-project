package com.loadburn.heron.storage.sql;

import com.loadburn.heron.storage.EntityStorage;
import com.loadburn.heron.storage.cache.ICache;
import com.loadburn.heron.storage.config.EntityMetadata;
import com.loadburn.heron.storage.config.EntityMetadataConfig;
import com.loadburn.heron.storage.convertion.ResultHandler;
import com.loadburn.heron.storage.exceptions.StorageException;
import com.loadburn.heron.storage.transaction.InTransaction;
import com.loadburn.heron.storage.transaction.impl.JdbcTransactionImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Map;

/**
 * @author slacrey (scstlinfeng@yahoo.com)
 *         Date: 13-12-12
 */
public abstract class SQLEntityStorage implements EntityStorage {

    private final Logger log = LoggerFactory.getLogger(SQLEntityStorage.class.getName());

    private final ICache cache;
    private final EntityMetadataConfig metadataConfig;
    private final Connection connection;

    public SQLEntityStorage(EntityMetadataConfig metadataConfig, ICache cache, Connection connection) {
        this.metadataConfig = metadataConfig;
        this.cache = cache;
        this.connection = connection;
    }

    @Override
    public boolean save(Class<?> entityClass, Object entity) throws SQLException {
        int result = executeUpdate(entityClass, entity, EntityMetadata.SAVE_STATEMENT);
        return result != 0;
    }

    @Override
    public boolean remove(Class<?> entityClass, Object entity) throws SQLException {
        int result = executeUpdate(entityClass, entity, EntityMetadata.REMOVE_STATEMENT);
        return result != 0;
    }

    @Override
    public boolean update(Class<?> entityClass, Object entity, Object primaryKey) throws SQLException {
        int result = executeUpdate(entityClass, entity, EntityMetadata.UPDATE_STATEMENT);
        return result != 0;
    }

    @Override
    public <T> T find(Class<?> entityClass, ResultHandler handler, Object primaryKey) throws SQLException {

        EntityMetadata.EntityDescriptor entityDescriptor = metadataConfig.getEntityDescriptor(entityClass);
        String findByIdStatement = metadataConfig.initOriginalQuerySQL(entityDescriptor, EntityMetadata.FIND_BY_ID_STATEMENT, false);
        Map<String, Object> nameParams = metadataConfig.initNameParamsMapOfPrimary(entityDescriptor, primaryKey);
        Query query = returnQuery(entityDescriptor, this.cache, this.connection, findByIdStatement);
        return (T) query.setParameterNameMap(nameParams).returnAs(handler).list();

    }

    @Override
    public <T> T find(Class<?> entityClass, ResultHandler handler) throws SQLException {
        EntityMetadata.EntityDescriptor entityDescriptor = metadataConfig.getEntityDescriptor(entityClass);
        String findAllStatement = metadataConfig.initOriginalQuerySQL(entityDescriptor, EntityMetadata.FIND_ALL_STATEMENT, false);
        Query query = returnQuery(entityDescriptor, this.cache, this.connection, findAllStatement);
        return (T) query.returnAs(handler).list();
    }

    @Override
    public Object getDelegate() {
        return new EntityStorageDelegate(this);
    }

    @Override
    public void close() {
        try {
            if (connection != null && !connection.isClosed()) {
                log.debug("connection close");
                connection.close();
            }
        } catch (Exception e) {
            log.error("Could not toggle connection close", e);
        }
    }

    @Override
    public boolean isOpen() {
        try {
            return connection != null && !connection.isClosed();
        } catch (SQLException e) {
            log.error("数据库连接获取关闭状态失败", e);
            return false;
        }
    }

    @Override
    public Query createQuery(Class<?> entityClass, String sqlQuery) {
        EntityMetadata.EntityDescriptor entityDescriptor = metadataConfig.getEntityDescriptor(entityClass);
        return returnQuery(entityDescriptor, this.cache, this.connection, sqlQuery);
    }

    @Override
    public Query createNamedQuery(Class<?> entityClass, String name) {
        EntityMetadata.EntityDescriptor entityDescriptor = metadataConfig.getEntityDescriptor(entityClass);
        String sqlQuery = metadataConfig.initOriginalQuerySQL(entityDescriptor, name, true);
        if (sqlQuery == null) {
            throw new StorageException(String.format("类中没有在@Queries或@Queries配置名称为[%s]的查询语句", name));
        }
        return returnQuery(entityDescriptor, this.cache, this.connection, sqlQuery);
    }

    @Override
    public InTransaction getTransaction() {
        return new JdbcTransactionImpl(connection);
    }

    // ============================================================

    public int executeUpdate(Class<?> entityClass, Object entity, String statement) throws SQLException {
        EntityMetadata.EntityDescriptor entityDescriptor = metadataConfig.getEntityDescriptor(entityClass);
        String saveStatement = metadataConfig.initOriginalQuerySQL(entityDescriptor, statement, false);
        Map<String, Object> nameParams = metadataConfig.initNameParamsMapOfInstance(entityDescriptor, entity);
        return returnQuery(entityDescriptor, this.cache, this.connection, saveStatement).setParameterNameMap(nameParams).executeUpdate();
    }

    public abstract Query returnQuery(EntityMetadata.EntityDescriptor entityDescriptor, ICache cache, Connection connection, String statement);

    // ==============================================

    public static class EntityStorageDelegate {
        private EntityStorage entityStorage;

        public EntityStorageDelegate(EntityStorage entityStorage) {
            this.entityStorage = entityStorage;
        }

        public EntityStorage getEntityStorage() {
            return entityStorage;
        }
    }

    public ICache getCache() {
        return cache;
    }

    public Connection getConnection() {
        return connection;
    }

    public EntityMetadataConfig getMetadataConfig() {
        return metadataConfig;
    }
}
