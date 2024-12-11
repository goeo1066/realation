package com.github.goeo1066.realation.core;

import com.github.goeo1066.realation.core.filter.WhereCreator;

import java.util.List;
import java.util.function.BiConsumer;

public interface DatabasePrompt<T, ID> {
    T select(ID id);

    List<T> selectBy(BiConsumer<T, WhereCreator> onWhere);

    T insert(T t);

    T update(T t);

    T delete(ID id);

    T upsert(T t);
}
