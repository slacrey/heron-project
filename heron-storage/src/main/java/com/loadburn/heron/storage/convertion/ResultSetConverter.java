package com.loadburn.heron.storage.convertion;

import com.google.common.base.Preconditions;
import com.google.common.collect.Maps;
import com.loadburn.heron.storage.config.EntityMetadata;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.*;
import java.util.*;

/**
 * @author slacrey (scstlinfeng@yahoo.com)
 *         Date: 13-11-2
 */
public class ResultSetConverter {
    private static final Map<Class<?>, Object> primitiveDefaults = new HashMap<Class<?>, Object>();
    private static final int PROPERTY_NOT_FOUND = -1;
    private EntityMetadata.EntityDescriptor entityDescriptor;

    static {
        //noinspection UnnecessaryBoxing
        primitiveDefaults.put(Integer.TYPE, Integer.valueOf(0));
        //noinspection UnnecessaryBoxing
        primitiveDefaults.put(Short.TYPE, Short.valueOf((short) 0));
        //noinspection UnnecessaryBoxing
        primitiveDefaults.put(Byte.TYPE, Byte.valueOf((byte) 0));
        //noinspection UnnecessaryBoxing
        primitiveDefaults.put(Float.TYPE, Float.valueOf(0f));
        //noinspection UnnecessaryBoxing
        primitiveDefaults.put(Double.TYPE, Double.valueOf(0d));
        //noinspection UnnecessaryBoxing
        primitiveDefaults.put(Long.TYPE, Long.valueOf(0L));
        primitiveDefaults.put(Boolean.TYPE, Boolean.FALSE);
        //noinspection UnnecessaryBoxing
        primitiveDefaults.put(Character.TYPE, Character.valueOf((char) 0));
    }

    private final Map<String, String> columnToPropertyOverrides;

    public ResultSetConverter(Map<String, String> columnToPropertyOverrides) {
        Preconditions.checkArgument(null == columnToPropertyOverrides, "传入数据库列Map不能为空");
        this.columnToPropertyOverrides = columnToPropertyOverrides;
    }

    public ResultSetConverter(EntityMetadata.EntityDescriptor entityDescriptor) {
        this.entityDescriptor = entityDescriptor;
        this.columnToPropertyOverrides = Maps.newHashMap();
    }

    /**
     * 获取类的属性描述器
     *
     * @param clazz 类
     * @return 返回属性描述器
     * @throws java.sql.SQLException
     */
    private PropertyDescriptor[] propertyDescriptors(Class<?> clazz)
            throws SQLException {
        BeanInfo beanInfo = null;
        try {
            beanInfo = Introspector.getBeanInfo(clazz);
        } catch (IntrospectionException e) {
            throw new SQLException(
                    "Bean introspection failed: " + e.getMessage());
        }
        return beanInfo.getPropertyDescriptors();
    }

    private void initColumnToProperty(PropertyDescriptor[] props, Class<?> clazz) {
        for (int i = 0; i < props.length; i++) {
            Map<String, EntityMetadata.EntityField> fieldMap = entityDescriptor.getFields();
            EntityMetadata.EntityField entityField = fieldMap.get(props[i].getName());
            if (entityField != null) {
                columnToPropertyOverrides.put(entityField.column(), props[i].getName());
            }
        }
    }

    private int[] mapColumnsToProperties(Class<?> clazz, ResultSetMetaData rsmd,
                                         PropertyDescriptor[] props) throws SQLException {

        int cols = rsmd.getColumnCount();
        int[] columnToProperty = new int[cols + 1];
        Arrays.fill(columnToProperty, PROPERTY_NOT_FOUND);

        for (int col = 1; col <= cols; col++) {
            String columnName = rsmd.getColumnLabel(col);
            if (null == columnName || 0 == columnName.length()) {
                columnName = rsmd.getColumnName(col);
            }
            String propertyName = columnToPropertyOverrides.get(columnName);
            if (propertyName == null) {
                propertyName = columnName;
            }
            for (int i = 0; i < props.length; i++) {

                if (propertyName.equalsIgnoreCase(props[i].getName())) {
                    columnToProperty[col] = i;
                    break;
                }
            }
        }

        return columnToProperty;
    }

    private <T> T newInstance(Class<T> c) throws SQLException {
        try {
            return c.newInstance();

        } catch (InstantiationException e) {
            throw new SQLException(
                    "Cannot create " + c.getName() + ": " + e.getMessage());
        } catch (IllegalAccessException e) {
            throw new SQLException(
                    "Cannot create " + c.getName() + ": " + e.getMessage());
        }
    }

    @SuppressWarnings("UnnecessaryBoxing")
    private Object processColumn(ResultSet rs, int index, Class<?> propType)
            throws SQLException {

        if (!propType.isPrimitive() && rs.getObject(index) == null) {
            return null;
        }

        if (propType.equals(String.class)) {
            return rs.getString(index);

        } else if (
                propType.equals(Integer.TYPE) || propType.equals(Integer.class)) {
            return Integer.valueOf(rs.getInt(index));

        } else if (
                propType.equals(Boolean.TYPE) || propType.equals(Boolean.class)) {
            return Boolean.valueOf(rs.getBoolean(index));

        } else if (propType.equals(Long.TYPE) || propType.equals(Long.class)) {
            return Long.valueOf(rs.getLong(index));

        } else if (
                propType.equals(Double.TYPE) || propType.equals(Double.class)) {
            return Double.valueOf(rs.getDouble(index));

        } else if (
                propType.equals(Float.TYPE) || propType.equals(Float.class)) {
            return Float.valueOf(rs.getFloat(index));

        } else if (
                propType.equals(Short.TYPE) || propType.equals(Short.class)) {
            return Short.valueOf(rs.getShort(index));

        } else if (propType.equals(Byte.TYPE) || propType.equals(Byte.class)) {
            return Byte.valueOf(rs.getByte(index));

        } else if (propType.equals(Timestamp.class)) {
            return rs.getTimestamp(index);

        } else if (propType.equals(SQLXML.class)) {
            return rs.getSQLXML(index);

        } else {
            return rs.getObject(index);
        }

    }

    private boolean isCompatibleType(Object value, Class<?> type) {
        // Do object check first, then primitives
        if (value == null || type.isInstance(value)) {
            return true;

        } else if (type.equals(Integer.TYPE) && Integer.class.isInstance(value)) {
            return true;

        } else if (type.equals(Long.TYPE) && Long.class.isInstance(value)) {
            return true;

        } else if (type.equals(Double.TYPE) && Double.class.isInstance(value)) {
            return true;

        } else if (type.equals(Float.TYPE) && Float.class.isInstance(value)) {
            return true;

        } else if (type.equals(Short.TYPE) && Short.class.isInstance(value)) {
            return true;

        } else if (type.equals(Byte.TYPE) && Byte.class.isInstance(value)) {
            return true;

        } else if (type.equals(Character.TYPE) && Character.class.isInstance(value)) {
            return true;

        } else if (type.equals(Boolean.TYPE) && Boolean.class.isInstance(value)) {
            return true;

        }
        return false;

    }

    private void callSetter(Object target, PropertyDescriptor prop, Object value)
            throws SQLException {

        Method setter = prop.getWriteMethod();

        if (setter == null) {
            return;
        }

        Class<?>[] params = setter.getParameterTypes();
        try {
            // convert types for some popular ones
            if (value instanceof java.util.Date) {
                final String targetType = params[0].getName();
                if ("java.sql.Date".equals(targetType)) {
                    value = new java.sql.Date(((java.util.Date) value).getTime());
                } else if ("java.sql.Time".equals(targetType)) {
                    value = new Time(((java.util.Date) value).getTime());
                } else if ("java.sql.Timestamp".equals(targetType)) {
                    value = new Timestamp(((java.util.Date) value).getTime());
                }
            }

            // Don't call setter if the value object isn't the right type
            if (this.isCompatibleType(value, params[0])) {
                //noinspection RedundantArrayCreation
                setter.invoke(target, new Object[]{value});
            } else {
                throw new SQLException(
                        "Cannot set " + prop.getName() + ": incompatible types, cannot convert "
                                + value.getClass().getName() + " to " + params[0].getName());
            }

        } catch (IllegalArgumentException e) {
            throw new SQLException(
                    "Cannot set " + prop.getName() + ": " + e.getMessage());

        } catch (IllegalAccessException e) {
            throw new SQLException(
                    "Cannot set " + prop.getName() + ": " + e.getMessage());

        } catch (InvocationTargetException e) {
            throw new SQLException(
                    "Cannot set " + prop.getName() + ": " + e.getMessage());
        }
    }

    private <T> T createBean(ResultSet rs, Class<T> type,
                             PropertyDescriptor[] props, int[] columnToProperty)
            throws SQLException {

        T bean = this.newInstance(type);

        for (int i = 1; i < columnToProperty.length; i++) {

            if (columnToProperty[i] == PROPERTY_NOT_FOUND) {
                continue;
            }

            PropertyDescriptor prop = props[columnToProperty[i]];
            Class<?> propType = prop.getPropertyType();

            Object value = this.processColumn(rs, i, propType);

            if (propType != null && value == null && propType.isPrimitive()) {
                value = primitiveDefaults.get(propType);
            }

            this.callSetter(bean, prop, value);
        }

        return bean;
    }

    public Map<String, Object> toMap(ResultSet rs) throws SQLException {
        Map<String, Object> result = new LowerCaseHashMap();
        ResultSetMetaData rsmd = rs.getMetaData();
        int cols = rsmd.getColumnCount();

        for (int i = 1; i <= cols; i++) {
            result.put(rsmd.getColumnName(i), rs.getObject(i));
        }

        return result;
    }

    public <T> List<T> toBeanList(ResultSet rs, Class<T> type) throws SQLException {
        List<T> results = new ArrayList<T>();

        if (!rs.next()) {
            return results;
        }

        PropertyDescriptor[] props = this.propertyDescriptors(type);
        ResultSetMetaData rsmd = rs.getMetaData();
        initColumnToProperty(props, type);
        int[] columnToProperty = this.mapColumnsToProperties(type, rsmd, props);

        do {
            results.add(this.createBean(rs, type, props, columnToProperty));
        } while (rs.next());

        return results;
    }

    public <T> T toBean(ResultSet rs, Class<T> type) throws SQLException {
        PropertyDescriptor[] props = this.propertyDescriptors(type);

        ResultSetMetaData rsmd = rs.getMetaData();
        initColumnToProperty(props, type);
        int[] columnToProperty = this.mapColumnsToProperties(type, rsmd, props);

        return this.createBean(rs, type, props, columnToProperty);
    }

    public Object[] toArray(ResultSet rs) throws SQLException {
        ResultSetMetaData meta = rs.getMetaData();
        int cols = meta.getColumnCount();
        Object[] result = new Object[cols];

        for (int i = 0; i < cols; i++) {
            result[i] = rs.getObject(i + 1);
        }

        return result;
    }

    private static class LowerCaseHashMap extends HashMap<String, Object> {

        private final Map<String, String> lowerCaseMap = Maps.newHashMap();

        @Override
        public Object get(Object key) {
            Object realKey = lowerCaseMap.get(key.toString().toLowerCase(Locale.ENGLISH));
            return super.get(realKey);
        }

        @Override
        public boolean containsKey(Object key) {
            Object realKey = lowerCaseMap.get(key.toString().toLowerCase(Locale.ENGLISH));
            return super.containsKey(realKey);
        }

        @Override
        public Object put(String key, Object value) {
            Object oldKey = lowerCaseMap.put(key.toLowerCase(Locale.ENGLISH), key);
            Object oldValue = super.remove(oldKey);
            super.put(key, value);
            return oldValue;
        }

        @Override
        public void putAll(Map<? extends String, ?> m) {
            for (Map.Entry<? extends String, ?> entry : m.entrySet()) {
                String key = entry.getKey();
                Object value = entry.getValue();
                this.put(key, value);
            }
        }

        @Override
        public Object remove(Object key) {
            Object realKey = lowerCaseMap.remove(key.toString().toLowerCase(Locale.ENGLISH));
            return super.remove(realKey);
        }

        @Override
        public void clear() {
            super.clear();
        }
    }

    public static ResultSetConverter delegation(final EntityMetadata.EntityDescriptor entityDescriptor) {
        return new ResultSetConverter(entityDescriptor);
    }
}
