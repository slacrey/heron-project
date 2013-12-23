package com.loadburn.heron.storage.config;

import com.google.common.collect.Maps;
import com.loadburn.heron.storage.exceptions.StorageException;

import java.util.Map;

/**
 * @author slacrey (scstlinfeng@yahoo.com)
 *         Date: 13-12-14
 */
public class EntityMetadataConfig {

    private final EntityMetadata entityMetadata;

    public EntityMetadataConfig(EntityMetadata entityMetadata) {
        this.entityMetadata = entityMetadata;
    }

    public EntityMetadata.EntityDescriptor getEntityDescriptor(Class<?> clazz) {
        return entityMetadata.getEntityClasses().get(clazz);
    }

    public Long getNextId(Class<?> strategy) {
        try {
            Object object = strategy.newInstance();
            if (object == null) {
                throw new StorageException("ID生成器调用错误");
            }
            IdGenerator idGenerator = (IdGenerator)object;
            return idGenerator.nextId();
        } catch (Exception e) {
            throw new StorageException("ID生成器调用错误", e);
        }
    }

    public  Map<String, Object> initNameParamsMapOfPrimary(EntityMetadata.EntityDescriptor entityDescriptor, Object primaryValue) {
        Map<String, Object> nameParams = Maps.newHashMap();
        String primaryKey = null;
        if (entityDescriptor != null) {
            primaryKey = entityDescriptor.getPrimaryKey();
            if (primaryKey != null) {
                nameParams.put(primaryKey, primaryValue);
            }
        }
        return nameParams;
    }

    /**
     * 获取原始的sql语句
     *
     * @param entityDescriptor 实体描述
     * @param name             语句的key
     * @param intact           语句key是否完整
     * @return
     */
    public String initOriginalQuerySQL(EntityMetadata.EntityDescriptor entityDescriptor, String name, Boolean intact) {

        if (entityDescriptor != null) {
            name = intact ? name : (name + entityDescriptor.getTableName());
            Map<String, String> queries = entityDescriptor.getQueriesStatement();
            String query = queries.get(name);
            if (query == null) {
                query = entityDescriptor.getModifiesStatement().get(name);
            }
            if (query == null) {
                query = entityDescriptor.getInternalStatement().get(name);
            }
            return query;
        } else {
            return null;
        }
    }

    public Map<String, Object> initNameParamsMapOfInstance(EntityMetadata.EntityDescriptor entityDescriptor, Object entity) {
        Map<String, Object> nameParams = Maps.newHashMap();
        Map<String, EntityMetadata.EntityField> entityFieldMap = null;
        if (entityDescriptor != null) {
            entityFieldMap = entityDescriptor.getFields();
            if (entityFieldMap != null) {
                for (Map.Entry<String, EntityMetadata.EntityField> entry : entityFieldMap.entrySet()) {
                    if (entry.getValue().primaryKey().isPrimaryKey()) {
                        Object object = entry.getValue().value(entity);
                        if (object == null) {
                            Class<?> strategy = entry.getValue().primaryKey().strategy().getClazz();
                            nameParams.put(entry.getKey(), getNextId(strategy));
                        } else {
                            nameParams.put(entry.getKey(), object);
                        }
                    } else {
                        nameParams.put(entry.getKey(), entry.getValue().value(entity));
                    }
                }
            }
        }
        return nameParams;
    }


}
