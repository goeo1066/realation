package com.github.goeo1066.realation.core;

import com.github.goeo1066.realation.select.SelectSpec;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;
import java.util.List;

public class DatabasePromptFactory {

    @SuppressWarnings({"unchecked"})
    public <S, ID, T extends DatabasePrompt<S, ID>> T create(Class<T> promptInterfaceClass, Class<S> entityClass, NamedParameterJdbcTemplate jdbcTemplate) throws NoSuchMethodException {
        DatabasePromptDelegate<S> delegate = new DatabasePromptDelegate<>(entityClass);
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
            }
            return InvocationHandler.invokeDefault(o, method, objects);
        });
    }

    @SuppressWarnings({"unchecked"})
    public <S, ID> DatabasePrompt<S, ID> create(Class<S> entityClass, NamedParameterJdbcTemplate jdbcTemplate) throws NoSuchMethodException {
        return (DatabasePrompt<S, ID>) create(DatabasePrompt.class, entityClass, jdbcTemplate);
    }
}
