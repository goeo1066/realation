package com.github.goeo1066.realation.core.filter;

import com.github.goeo1066.realation.core.ColumnInfo;
import com.github.goeo1066.realation.core.TableInfo;
import net.bytebuddy.ByteBuddy;
import net.bytebuddy.description.NamedElement;
import net.bytebuddy.implementation.MethodDelegation;
import net.bytebuddy.matcher.ElementMatcher;
import net.bytebuddy.matcher.ElementMatchers;

import java.lang.reflect.InvocationTargetException;
import java.util.List;

public interface WhereCreator {
    <S> void create(S condition);

    static <S> S WhereCreatorCondition(TableInfo<S> tableInfo) {
        ByteBuddy byteBuddy = new ByteBuddy();
        Class<? extends S> abstractTrackerClass = null;
        try (var make = byteBuddy.subclass(tableInfo.getEntityClass())
                .method(elementMatherWithNames(tableInfo))
                .intercept(MethodDelegation.to(new WhereTracker()))
                .make()) {
            abstractTrackerClass = make.load(tableInfo.getEntityClass().getClassLoader())
                    .getLoaded();
        }

        if (abstractTrackerClass != null) {
            try {
                return (S) abstractTrackerClass.getConstructor().newInstance();
            } catch (InstantiationException | IllegalAccessException | InvocationTargetException |
                     NoSuchMethodException e) {
                throw new RuntimeException(e);
            }
        }

        return null;
    }

    static <S, T extends NamedElement> ElementMatcher.Junction<T> elementMatherWithNames(TableInfo<S> tableInfo) {
        String[] names = new String[tableInfo.getColumnInfoList().size()];
        List<ColumnInfo> columnInfoList = tableInfo.getColumnInfoList();
        for (int i = 0; i < columnInfoList.size(); i++) {
            ColumnInfo columnInfo = columnInfoList.get(i);
            names[i] = columnInfo.getGetterMethod().getName();
        }
        return ElementMatchers.namedOneOf(names);
    }
}


