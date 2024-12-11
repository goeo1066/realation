package com.github.goeo1066.realation.core.filter;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class ColumnHolder {
    private final LinkedList<String> columnBuffer = new LinkedList<>();

    public void add(String column) {
        columnBuffer.add(column);
    }

    public void reset() {
        columnBuffer.clear();
    }

    public List<String> getAll() {
        return new ArrayList<>(columnBuffer);
    }

    public List<String> getAllAndReset() {
        List<String> result = new ArrayList<>(columnBuffer);
        reset();
        return result;
    }

    public String getOneAndRemove() {
        return columnBuffer.poll();
    }
}
