package com.github.goeo1066.realation.core;

import org.springframework.data.relational.core.mapping.Table;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.lang.NonNull;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.util.function.Function;

public class TableInfo<S> {
    private final Class<S> entityClass;
    private final String schema;
    private final String tableName;
    private final List<ColumnInfo> columnInfoList;
    private final List<ColumnInfo> primaryKeyInfoList;
    private final RowMapper<S> rowMapper;

    public TableInfo(Class<S> entityClass, String schema, String tableName, List<ColumnInfo> columnInfoList, List<ColumnInfo> primaryKeyInfoList, RowMapper<S> rowMapper) {
        this.entityClass = entityClass;
        this.schema = schema;
        this.tableName = tableName;
        this.columnInfoList = columnInfoList;
        this.primaryKeyInfoList = primaryKeyInfoList;
        this.rowMapper = rowMapper;
    }

    public Class<S> getEntityClass() {
        return entityClass;
    }

    public String getSchema() {
        return schema;
    }

    public String getTableName() {
        return tableName;
    }

    public List<ColumnInfo> getColumnInfoList() {
        return columnInfoList;
    }

    public List<ColumnInfo> getPrimaryKeyInfoList() {
        return primaryKeyInfoList;
    }

    public RowMapper<S> getRowMapper() {
        return rowMapper;
    }

    public String tableFullName() {
        if (schema == null || schema.isBlank()) {
            return tableName;
        }
        return schema + "." + tableName;
    }

    public static <S> TableInfo<S> retrieveFromEntity(Class<S> entityClass) throws NoSuchMethodException {
        Table table = entityClass.getAnnotation(Table.class);
        if (table == null) {
            throw new RuntimeException("no @Table annotation found");
        }

        String schema = table.schema();
        String tableName = table.name();

        if (tableName == null || tableName.isBlank()) {
            tableName = RealationUtils.lowerCamelToSnake(entityClass.getName(), true);
        }

        List<ColumnInfo> columnInfoList = ColumnInfo.retrieveColumnInfo(entityClass);
        List<ColumnInfo> primaryKeyInfoList = columnInfoList.stream().filter(it -> it.isPrimaryKey()).toList();
        var rowMapper = getRowMapperForEntity(entityClass, columnInfoList);

        return new TableInfo<>(entityClass, schema, tableName, columnInfoList, primaryKeyInfoList, rowMapper);
    }

    @SuppressWarnings("rawtypes")
    public static <S> RowMapper<S> getRowMapperForEntity(Class<S> entityClass, List<ColumnInfo> columnInfoList) throws NoSuchMethodException {
        Map<String, Function<ResultSet, ?>> columnNameResultSetterMap = new HashMap<>();
        for (ColumnInfo columnInfo : columnInfoList) {
            Class fieldType = columnInfo.getFieldType();
            String columnName = columnInfo.getColumnName();
            if (fieldType == String.class) {
                columnNameResultSetterMap.put(columnName, getDataRetrieverString(columnName, null));
            } else if (fieldType == Long.class) {
                columnNameResultSetterMap.put(columnName, getDataRetrieverLong(columnName, null));
            } else if (fieldType == long.class) {
                columnNameResultSetterMap.put(columnName, getDataRetrieverLong(columnName, 0L));
            } else if (fieldType == Integer.class) {
                columnNameResultSetterMap.put(columnName, getDataRetrieverInt(columnName, null));
            } else if (fieldType == int.class) {
                columnNameResultSetterMap.put(columnName, getDataRetrieverInt(columnName, 0));
            }
        }

        return new RowMapper<S>() {
            private Set<String> existingColumns = null;

            @Override
            public S mapRow(@NonNull ResultSet rs, int rowNum) throws SQLException {
                if (existingColumns == null) {
                    existingColumns = loadExistingColumns(rs);
                }
                try {
                    Constructor<S> constructor = entityClass.getConstructor();
                    S instance = constructor.newInstance();
                    for (ColumnInfo columnInfo : columnInfoList) {
                        String columnName = columnInfo.getColumnName();
                        if (existingColumns.contains(columnName.toUpperCase())) {
                            Function function = columnNameResultSetterMap.get(columnName);
                            @SuppressWarnings("unchecked")
                            Object value = function.apply(rs);
                            columnInfo.getSetterMethod().invoke(instance, value);
                        }
                    }
                    return instance;
                } catch (NoSuchMethodException | InvocationTargetException | InstantiationException |
                         IllegalAccessException e) {
                    throw new RuntimeException(e);
                }
            }
        };
    }

    @SuppressWarnings("rawtypes")
    public static <S> RowMapper<S> getRowMapperForRecord(Class<S> clazz, List<ColumnInfo> columnInfoList) throws NoSuchMethodException {

        Function[] retrievers = new Function[columnInfoList.size()];
        Class[] fieldTypes = new Class[columnInfoList.size()];
        for (int i = 0; i < columnInfoList.size(); i++) {
            ColumnInfo columnInfo = columnInfoList.get(i);
            Class fieldType = columnInfo.getFieldType();
            String columnName = columnInfo.getColumnName();
            if (fieldType == String.class) {
                retrievers[i] = getDataRetrieverString(columnName, null);
            } else if (fieldType == Long.class) {
                retrievers[i] = getDataRetrieverLong(columnName, null);
            } else if (fieldType == long.class) {
                retrievers[i] = getDataRetrieverLong(columnName, 0L);
            } else if (fieldType == Integer.class) {
                retrievers[i] = getDataRetrieverInt(columnName, null);
            } else if (fieldType == int.class) {
                retrievers[i] = getDataRetrieverInt(columnName, 0);
            } else {
                throw new RuntimeException("Unsupported field type: " + fieldType);
            }
            fieldTypes[i] = fieldType;
        }

        var constructor = clazz.getConstructor(fieldTypes);
        return new RowMapper<>() {
            private Set<String> existingColumns = null;

            @Override
            @SuppressWarnings("unchecked")
            public S mapRow(@NonNull ResultSet rs, int rowNum) throws SQLException {
                if (existingColumns == null) {
                    existingColumns = loadExistingColumns(rs);
                }
                Object[] initializers = new Object[columnInfoList.size()];
                for (int i = 0; i < columnInfoList.size(); i++) {
                    String columnName = columnInfoList.get(i).getColumnName();
                    if (!existingColumns.contains(columnName.toUpperCase()) || retrievers[i] == null) {
                        initializers[i] = getNullOrDefault(columnInfoList.get(i).getFieldType());
                    } else {
                        initializers[i] = retrievers[i].apply(rs);
                    }
                }
                try {
                    return constructor.newInstance(initializers);
                } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
                    throw new RuntimeException(e);
                }
            }
        };
    }

    private static Set<String> loadExistingColumns(ResultSet rs) throws SQLException {
        int columnCount = rs.getMetaData().getColumnCount();
        Set<String> result = new HashSet<>();
        for (int i = 1; i <= columnCount; i++) {
            result.add(rs.getMetaData().getColumnName(i).toUpperCase());
        }
        return Collections.unmodifiableSet(result);
    }

    private static Object getNullOrDefault(Class<?> clazz) {
        if (clazz == int.class) {
            return 0;
        } else if (clazz == short.class) {
            return 0;
        } else if (clazz == byte.class) {
            return 0;
        } else if (clazz == long.class) {
            return 0L;
        } else if (clazz == double.class) {
            return null;
        } else if (clazz == float.class) {
            return null;
        } else if (clazz == boolean.class) {
            return false;
        } else if (clazz == char.class) {
            return '\u0000';
        } else {
            return null;
        }
    }

    private static Function<ResultSet, String> getDataRetrieverString(String columnName, String defaultValue) {
        return getDataRetriever(columnName, defaultValue, ResultSet::getString);
    }

    private static Function<ResultSet, Long> getDataRetrieverLong(String columnName, Long defaultValue) {
        return getDataRetriever(columnName, defaultValue, ResultSet::getLong);
    }

    private static Function<ResultSet, Integer> getDataRetrieverInt(String columnName, Integer defaultValue) {
        return getDataRetriever(columnName, defaultValue, ResultSet::getInt);
    }

    private static <T> Function<ResultSet, T> getDataRetriever(String columnName, T defaultValue, ThrowableFunction<ResultSet, String, T, Throwable> getter) {
        return rs -> {
            try {
                T value = getter.apply(rs, columnName);
                if (value == null || rs.wasNull()) {
                    return defaultValue;
                }
                return value;
            } catch (Throwable e) {
                throw new RuntimeException(e);
            }
        };
    }

    public static class Builder<S> {
        private Class<S> entityClass;
        private String schema;
        private String tableName;
        private List<ColumnInfo> columnInfoList;
        private List<ColumnInfo> primaryKeyInfoList;
        private RowMapper<S> rowMapper;

        public TableInfo<S> build() {
            return new TableInfo<>(
                    entityClass,
                    schema,
                    tableName,
                    columnInfoList,
                    primaryKeyInfoList,
                    rowMapper
            );
        }

        public Builder<S> entityClass(Class<S> entityClass) {
            this.entityClass = entityClass;
            return this;
        }

        public Builder<S> schema(String schema) {
            this.schema = schema;
            return this;
        }

        public Builder<S> tableName(String tableName) {
            this.tableName = tableName;
            return this;
        }

        public Builder<S> columnInfoList(List<ColumnInfo> columnInfoList) {
            this.columnInfoList = columnInfoList;
            return this;
        }

        public Builder<S> primaryKeyInfoList(List<ColumnInfo> primaryKeyInfoList) {
            this.primaryKeyInfoList = primaryKeyInfoList;
            return this;
        }

        public Builder<S> rowMapper(RowMapper<S> rowMapper) {
            this.rowMapper = rowMapper;
            return this;
        }
    }
}
