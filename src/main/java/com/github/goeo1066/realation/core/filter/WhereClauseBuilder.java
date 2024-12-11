package com.github.goeo1066.realation.core.filter;

import com.github.goeo1066.realation.core.ColumnInfo;
import com.github.goeo1066.realation.core.TableInfo;

import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

public class WhereClauseBuilder {
    private final List<Object> placeholders;
    private final List<Class<?>> placeholderTypes;
    private final StringBuilder stringBuilder;
    private final ColumnHolder columnHolder;
    private final MethodToColumnNameMap methodToColumnNameMap;
    private final List<String> orderBy = new ArrayList<>();

    public WhereClauseBuilder(List<Object> placeholders, List<Class<?>> placeholderTypes, StringBuilder stringBuilder, ColumnHolder columnHolder, MethodToColumnNameMap methodToColumnNameMap) {
        this.placeholders = placeholders;
        this.placeholderTypes = placeholderTypes;
        this.stringBuilder = stringBuilder;
        this.columnHolder = columnHolder;
        this.methodToColumnNameMap = methodToColumnNameMap;
    }

    public WhereClauseBuilder() {
        this(new ArrayList<>(), new ArrayList<>(), new StringBuilder(), new ColumnHolder(), new MethodToColumnNameMap());
    }

    public List<Object> getPlaceholders() {
        return placeholders;
    }

    public List<Class<?>> getPlaceholderTypes() {
        return placeholderTypes;
    }

    public Object[] getPlaceholdersAsArray() {
        Object[] result = new Object[placeholders.size()];
        for (int i = 0; i < placeholders.size(); i++) {
            result[i] = placeholders.get(i);
        }
        return result;
    }

    public int[] getPlaceholderTypesAsArray() {
        int[] result = new int[placeholderTypes.size()];
        for (int i = 0; i < placeholderTypes.size(); i++) {
            Class clazz = placeholderTypes.get(i);
            if (clazz == String.class) {
                result[i] = Types.VARCHAR;
            } else if (clazz == Long.class || clazz == long.class || clazz == Integer.class || clazz == int.class) {
                result[i] = Types.INTEGER;
            }
        }
        return result;
    }

    public StringBuilder getStringBuilder() {
        return stringBuilder;
    }

    public ColumnHolder getColumnHolder() {
        return columnHolder;
    }

    public MethodToColumnNameMap getMethodToColumnNameMap() {
        return methodToColumnNameMap;
    }

    public List<String> getOrderBy() {
        return new ArrayList<>(orderBy);
    }

    public String toPreparedStatement() {
        return stringBuilder.toString();
    }

    public String toOrderByClause() {
        return String.join(", ", orderBy);
    }

    public List<Object> toReplacement() {
        return new ArrayList<>(placeholders);
    }

    public <S> void buildMethodToColumnNameMap(TableInfo<S> tableInfo) {
        for (ColumnInfo columnInfo : tableInfo.getColumnInfoList()) {
            methodToColumnNameMap.put(columnInfo.getGetterMethod().getName(), columnInfo.getColumnName());
        }
    }

    public void addOrderBy(String column, String direction) {
        orderBy.add(column + " " + direction);
    }
}
