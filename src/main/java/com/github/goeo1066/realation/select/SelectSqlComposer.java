package com.github.goeo1066.realation.select;

import com.github.goeo1066.realation.core.TableInfo;

public interface SelectSqlComposer<S> {
    String composeSelectSql(TableInfo<S> tableInfo, SelectSpec selectSpec);

    String composeCountSql(TableInfo<S> tableInfo, SelectSpec selectSpec);

    String composeSelectTestSql(TableInfo<S> tableInfo);

    static <S> SelectSqlComposer<S> createInstanceOf(String dbType) {
        if (dbType.equals("postgresql")) {
            return new SelectSqlComposerPostgreSQL<>();
        }
        throw new RuntimeException("Unsupported database type: " + dbType);
    }
}
