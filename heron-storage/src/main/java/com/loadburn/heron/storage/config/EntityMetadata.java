package com.loadburn.heron.storage.config;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.loadburn.heron.storage.annotations.*;
import com.loadburn.heron.utils.StringUtils;

import javax.persistence.Column;
import javax.persistence.Table;
import javax.persistence.Transient;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.*;
import java.util.regex.Pattern;

/**
 * @author slacrey (scstlinfeng@yahoo.com)
 *         Date: 13-12-12
 */
@Singleton
public class EntityMetadata {

    public final static Pattern NAMED_ARG_PATTERN = Pattern.compile("(@[\\w_]+)",
            Pattern.CASE_INSENSITIVE | Pattern.MULTILINE);
    public final static String SAVE_STATEMENT = "save_internal_";
    public final static String REMOVE_STATEMENT = "remove_internal_";
    public final static String UPDATE_STATEMENT = "update_internal_";
    public final static String FIND_ALL_STATEMENT = "find_all_internal_";
    public final static String FIND_BY_ID_STATEMENT = "find_by_id_internal_";
    public final static String STORAGE_CACHE_NAME = "com.loadburn.heron.storage";

    public final static List<String> cacheStatement = Lists.newArrayList();

    private Map<Class<?>, EntityDescriptor> entityClasses;
    private final DataBaseType dataBaseType;

    public static List<String> getCacheStatement() {
        return cacheStatement;
    }

    @Inject
    public EntityMetadata(DataBaseType dataBaseType, Set<Class<?>> classes) {
        this.dataBaseType = dataBaseType;
        entityClasses = Maps.newHashMap();
        for (Class<?> clazz : classes) {
            entityClasses.put(clazz, new EntityDescriptor(clazz));
        }
    }

    public Map<Class<?>, EntityDescriptor> getEntityClasses() {
        return entityClasses;
    }

    public class EntityDescriptor {
        private final String primaryKey;
        private final Map<String, EntityField> fields;
        private final Class<?> entityType;
        private final Collection<Annotation> classAnnotations = new ArrayList<Annotation>();

        private final String tableName;
        private final EntityCache entityCache;
        private final Map<String, String> modifiesStatement = Maps.newHashMap();
        private final Map<String, String> queriesStatement = Maps.newHashMap();
        private final Map<String, String> internalStatement = Maps.newHashMap();

        public EntityDescriptor(final Class<?> clazz) {
            String primaryKey = null;
            this.entityType = clazz;

            Table table = clazz.getAnnotation(Table.class);
            if (null != table) {
                this.tableName = table.name();
            } else {
                this.tableName = StringUtils.underscoreName(clazz.getSimpleName());
            }

            final Cache cacheAnnotation = clazz.getAnnotation(Cache.class);
            if (null != cacheAnnotation) {
                entityCache = new EntityCache() {
                    @Override
                    public String name() {
                        if (!StringUtils.empty(cacheAnnotation.region())) {
                            return cacheAnnotation.region();
                        } else {
                            return StringUtils.underscoreName(clazz.getSimpleName());
                        }
                    }

                    @Override
                    public boolean cache() {
                        return true;
                    }
                };
            } else {
                entityCache = new EntityCache() {
                    @Override
                    public String name() {
                        return null;
                    }

                    @Override
                    public boolean cache() {
                        return false;
                    }
                };
            }

            Collections.addAll(this.classAnnotations, clazz.getAnnotations());

            Map<String, EntityField> fields = new HashMap<String, EntityField>();
            for (final Field field : clazz.getDeclaredFields()) {
                if (Modifier.isTransient(field.getModifiers()))
                    continue;

                final Field tempField = field;
                final List<Annotation> annotations = Lists.newArrayList();
                Collections.addAll(annotations, field.getAnnotations());
                // TODO(linfeng) 待添加ID生成策略d
                final Id idAnnotation = field.getAnnotation(Id.class);
                if (idAnnotation != null) {
                    primaryKey = StringUtils.underscoreName(field.getName());
                }

                // 设置private为可访问
                if (!field.isAccessible())
                    field.setAccessible(true);

                Transient tran = field.getAnnotation(Transient.class);
                if (tran == null) {
                    fields.put(field.getName(), new EntityField() {
                        @Override
                        public Object value(Object from) {
                            try {
                                return tempField.get(from);
                            } catch (IllegalAccessException e) {
                                throw new RuntimeException(e);
                            }
                        }

                        @Override
                        public Class<?> type() {
                            return tempField.getType();
                        }

                        @Override
                        public String column() {
                            Column column = tempField.getAnnotation(Column.class);
                            if (null != column && column.name() != null) {
                                return column.name();
                            } else {
                                return StringUtils.underscoreName(tempField.getName());
                            }
                        }

                        @Override
                        public Collection<Annotation> annotations() {
                            return annotations;
                        }

                        @Override
                        public PrimaryKey primaryKey() {
                            final Id idAnnotation = tempField.getAnnotation(Id.class);
                            return new PrimaryKey() {
                                @Override
                                public boolean isPrimaryKey() {
                                    if (idAnnotation != null) {
                                        return true;
                                    }
                                    return false;
                                }

                                @Override
                                public GeneratorStrategy strategy() {
                                    if (idAnnotation != null) {
                                        return idAnnotation.strategy();
                                    }
                                    return null;
                                }
                            };
                        }
                    });
                }
            }

            if (primaryKey == null)
                throw new IllegalStateException("Entity class missing Primary Key. At least one" +
                        " serializable field must be marked with @Id but none found for: " + clazz.getName());

            this.fields = Collections.unmodifiableMap(fields);
            this.primaryKey = primaryKey;

            initStatement(clazz);

            initInternalStatement(this.tableName, this.primaryKey, this.fields);

        }

        private void initStatement(Class<?> clazz) {
            final Queries queryAnnotation = clazz.getAnnotation(Queries.class);
            if (null != queryAnnotation) {
                if (dataBaseType.equals(queryAnnotation.scheme())) {
                    for (Query query : queryAnnotation.value()) {
                        this.queriesStatement.put(query.name(), query.query());
                    }
                }
            }

            final Modifies modifyAnnotation = clazz.getAnnotation(Modifies.class);
            if (null != modifyAnnotation) {
                if (dataBaseType.equals(modifyAnnotation.scheme())) {
                    for (Modify modify : modifyAnnotation.value()) {
                        this.modifiesStatement.put(modify.name(), modify.modify());
                    }
                }
            }

        }

        /**
         * 初始化内部语句
         *
         * @param tableName  表名
         * @param primaryKey 主键
         * @param fields     类属性map
         */
        private void initInternalStatement(String tableName, String primaryKey, Map<String, EntityField> fields) {
            StringBuilder columnsBuilder = new StringBuilder();
            StringBuilder valuesBuilder = new StringBuilder();
            StringBuilder setsBuilder = new StringBuilder();
            StringBuilder primaryBuilder = new StringBuilder();
            StringBuilder primaryValueBuilder = new StringBuilder();
            StringBuilder primarySetsBuilder = new StringBuilder();
            for (Map.Entry<String, EntityField> entry : fields.entrySet()) {

                if (primaryKey.equals(entry.getValue().column())) {
                    primaryBuilder.append(entry.getValue().column());
                    primaryValueBuilder.append("@").append(entry.getKey());
                    primarySetsBuilder.append(entry.getValue().column()).append("=").append("@").append(entry.getKey());
                } else {
                    columnsBuilder.append(entry.getValue().column()).append(",");
                    valuesBuilder.append("@").append(entry.getKey()).append(",");
                    setsBuilder.append(entry.getValue().column()).append("=").append("@").append(entry.getKey()).append(",");
                }
            }

            String columns = columnsBuilder.deleteCharAt(columnsBuilder.length() - 1).toString();
            String values = valuesBuilder.deleteCharAt(valuesBuilder.length() - 1).toString();
            String sets = setsBuilder.deleteCharAt(setsBuilder.length() - 1).toString();

            StringBuilder saveStatement = new StringBuilder("INSERT INTO ");
            saveStatement.append(tableName).append("(").append(primaryBuilder).append(",").append(columns).append(")")
                    .append(" ").append("VALUES(").append(primaryValueBuilder).append(",").append(values).append(")");

            this.internalStatement.put(SAVE_STATEMENT + tableName, saveStatement.toString());

            StringBuilder updateStatement = new StringBuilder("UPDATE ");
            updateStatement.append(tableName).append(" SET ").append(sets).append(" WHERE ").append(primarySetsBuilder);
            this.internalStatement.put(UPDATE_STATEMENT + tableName, updateStatement.toString());

            StringBuilder findAllStatement = new StringBuilder("SELECT ");
            findAllStatement.append(primaryBuilder).append(",").append(columns).append(" FROM ").append(tableName);
            this.queriesStatement.put(FIND_ALL_STATEMENT + tableName, findAllStatement.toString());

            StringBuilder findByIdStatement = new StringBuilder("SELECT ");
            findByIdStatement.append(primaryBuilder).append(",").append(columns).append(" FROM ").append(tableName)
                    .append(" WHERE ").append(primarySetsBuilder);
            this.queriesStatement.put(FIND_BY_ID_STATEMENT + tableName, findByIdStatement.toString());

            StringBuilder deleteStatement = new StringBuilder("DELETE FROM ");
            deleteStatement.append(tableName).append(" WHERE ").append(primarySetsBuilder);
            this.internalStatement.put(REMOVE_STATEMENT + tableName, deleteStatement.toString());

        }

        public Map<String, EntityField> getFields() {
            return fields;
        }

        public String getPrimaryKey() {
            return primaryKey;
        }

        public Class<?> getEntityType() {
            return entityType;
        }

        public Collection<Annotation> getClassAnnotations() {
            return classAnnotations;
        }

        public String getTableName() {
            return tableName;
        }

        public EntityCache getEntityCache() {
            return entityCache;
        }

        public Map<String, String> getModifiesStatement() {
            return modifiesStatement;
        }

        public Map<String, String> getQueriesStatement() {
            return queriesStatement;
        }

        public Map<String, String> getInternalStatement() {
            return internalStatement;
        }
    }

    public static interface EntityCache {
        String name();

        boolean cache();
    }

    public static interface EntityField {

        Object value(Object from);

        Class<?> type();

        String column();

        public Collection<Annotation> annotations();

        PrimaryKey primaryKey();

    }

    public static interface PrimaryKey {

        boolean isPrimaryKey();

        GeneratorStrategy strategy();

    }

}
