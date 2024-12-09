package com.github.goeo1066.realation.core;

import com.github.goeo1066.realation.core.filter.WhereClauses;
import com.github.goeo1066.realation.core.filter.WhereCreator;
import com.github.goeo1066.realation.select.SelectSpec;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;
import java.util.List;
import java.util.function.BiConsumer;

public class DatabasePromptFactory {

    @SuppressWarnings({"unchecked"})
    public <S, ID, T extends DatabasePrompt<S, ID>> T create(Class<T> promptInterfaceClass, Class<S> entityClass, NamedParameterJdbcTemplate jdbcTemplate) throws NoSuchMethodException {
        TableInfo<S> tableInfo = TableInfo.retrieveFromEntity(entityClass);
        DatabasePromptDelegate<S> delegate = new DatabasePromptDelegate<>(tableInfo);
        return (T) Proxy.newProxyInstance(promptInterfaceClass.getClassLoader(), new Class[]{promptInterfaceClass}, (o, method, objects) -> {
            switch (method.getName()) {
                case "select" -> {
                    SelectSpec selectSpec = new SelectSpec("IDX = " + objects[0].toString(), null, null, null);
                    List<S> result = delegate.select(jdbcTemplate, selectSpec);
                    if (result == null || result.isEmpty()) {
                        return null;
                    }
                    return result.get(0);
                }
                case "selectBy" -> {
                    S trackerEntity = WhereCreator.WhereCreatorCondition(tableInfo);
                    BiConsumer<S, WhereCreator> onWhere = (BiConsumer<S, WhereCreator>) objects[0];
                    onWhere.accept(trackerEntity, new WhereCreator());
                    return null;
                }
            }
            return InvocationHandler.invokeDefault(o, method, objects);
        });
    }

    @SuppressWarnings({"unchecked"})
    public <S, ID> DatabasePrompt<S, ID> create(Class<S> entityClass, NamedParameterJdbcTemplate jdbcTemplate) throws NoSuchMethodException {
        return (DatabasePrompt<S, ID>) create(DatabasePrompt.class, entityClass, jdbcTemplate);
    }
}
