package com.github.goeo1066.realation.core;

import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

public class ColumnInfo {
    private final String columnName;
    private final Field field;
    private final Method getterMethod;
    private final Method setterMethod;
    private final Class<?> fieldType;
    private final boolean isPrimaryKey;
    private final boolean isTransient;

    public ColumnInfo(String columnName, Field field, Method getterMethod, Method setterMethod, Class<?> fieldType, boolean isPrimaryKey, boolean isTransient) {
        this.columnName = columnName;
        this.field = field;
        this.getterMethod = getterMethod;
        this.setterMethod = setterMethod;
        this.fieldType = fieldType;
        this.isPrimaryKey = isPrimaryKey;
        this.isTransient = isTransient;
    }

    public String getColumnName() {
        return columnName;
    }

    public Field getField() {
        return field;
    }

    public Method getGetterMethod() {
        return getterMethod;
    }

    public Method getSetterMethod() {
        return setterMethod;
    }

    public Class<?> getFieldType() {
        return fieldType;
    }

    public boolean isPrimaryKey() {
        return isPrimaryKey;
    }

    public boolean isTransient() {
        return isTransient;
    }

    public static List<ColumnInfo> retrieveColumnInfo(Class<?> clazz) {
        List<ColumnInfo> columnInfoList = new ArrayList<>(clazz.getDeclaredFields().length);
        for (Method method : clazz.getMethods()) {
            String methodName = method.getName();
            int paramCount = method.getParameterCount();
            Class<?> returnType = method.getReturnType();
            if ("getClass".equals(methodName) && returnType == Class.class) {
                continue;
            }

            if (paramCount != 0 || returnType == void.class || returnType == Void.class) {
                continue;
            }

            String propertyName = null;
            if (returnType == boolean.class || returnType == Boolean.class) {
                if (methodName.startsWith("is")) {
                    propertyName = methodName.substring(2);
                }
            } else {
                if (methodName.startsWith("get")) {
                    propertyName = methodName.substring(3);
                }
            }

            if (propertyName == null) {
                continue;
            }

            String setterName = "set" + propertyName;
            propertyName = RealationUtils.upperCamelToLowerCamel(propertyName);
            String columnName = RealationUtils.lowerCamelToSnake(propertyName, true);
            Field field = null;
            try {
                field = clazz.getDeclaredField(propertyName);
            } catch (NoSuchFieldException ignored) {

            }
            boolean isPrimaryKey = false;
            boolean isTransient = false;

            if (field != null) {
                isPrimaryKey = field.isAnnotationPresent(Id.class);
                isTransient = field.isAnnotationPresent(Transient.class);
            }

            if (!isPrimaryKey) {
                isPrimaryKey = method.isAnnotationPresent(Id.class);
            }
            if (!isTransient) {
                isTransient = method.isAnnotationPresent(Transient.class);
            }
            Method setterMethod = null;
            try {
                setterMethod = clazz.getMethod(setterName, returnType);
            } catch (NoSuchMethodException ignored) {

            }

            var lazorColumnInfo = new ColumnInfo(columnName, field, method, setterMethod, returnType, isPrimaryKey, isTransient);
            columnInfoList.add(lazorColumnInfo);
        }
        return columnInfoList;
    }

    public static class Builder {
        private String columnName;
        private Field fieldName;
        private Method getterName;
        private Method setterName;
        private Class<?> fieldType;
        private boolean isPrimaryKey;
        private boolean isTransient;

        public ColumnInfo build() {
            return new ColumnInfo(
                    columnName,
                    fieldName,
                    getterName,
                    setterName,
                    fieldType,
                    isPrimaryKey,
                    isTransient
            );
        }

        public Builder columnName(String columnName) {
            this.columnName = columnName;
            return this;
        }

        public Builder fieldName(Field fieldName) {
            this.fieldName = fieldName;
            return this;
        }

        public Builder getterName(Method getterName) {
            this.getterName = getterName;
            return this;
        }

        public Builder setterName(Method setterName) {
            this.setterName = setterName;
            return this;
        }

        public Builder fieldType(Class<?> fieldType) {
            this.fieldType = fieldType;
            return this;
        }

        public Builder isPrimaryKey(boolean isPrimaryKey) {
            this.isPrimaryKey = isPrimaryKey;
            return this;
        }

        public Builder isTransient(boolean isTransient) {
            this.isTransient = isTransient;
            return this;
        }
    }
}
