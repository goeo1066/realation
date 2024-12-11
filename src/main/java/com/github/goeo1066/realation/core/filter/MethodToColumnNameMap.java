package com.github.goeo1066.realation.core.filter;

import java.util.HashMap;
import java.util.Map;

public class MethodToColumnNameMap {
    private final Map<String, String> map = new HashMap<>();

    public void put(String methodName, String columnName) {
        map.put(methodName, columnName);
    }

    public String get(String methodName) {
        return map.get(methodName);
    }
}
