package com.github.goeo1066.realation.core;

public interface DatabasePrompt<T, ID> {
    T select(ID id);

    T insert(T t);

    T update(T t);

    T delete(ID id);

    T upsert(T t);
}
