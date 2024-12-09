package com.github.goeo1066.realation.select;

import com.github.goeo1066.realation.core.TableInfo;

public class SelectSqlComposerPostgreSQL<S> implements SelectSqlComposer<S> {

    @Override
    public String composeSelectSql(TableInfo<S> tableInfo, SelectSpec selectSpec) {
        return createSelectSql(tableInfo, "T", selectSpec);
    }

    @Override
    public String composeCountSql(TableInfo<S> tableInfo, SelectSpec selectSpec) {
        return createCountSql(tableInfo, "T", selectSpec);
    }

    @Override
    public String composeSelectTestSql(TableInfo<S> tableInfo) {
        SelectSpec selectSpec = SelectSpec.builder()
                .whereClause("1 = 0")
                .build();
        return createSelectSql(tableInfo, "T", selectSpec);
    }

    private String createSelectSql(TableInfo<S> tableInfo, String mainTableAlias, SelectSpec selectSpec) {
        return createSelectSqlTemplate(tableInfo, mainTableAlias, "*", selectSpec);
    }

    private String createCountSql(TableInfo<S> tableInfo, String mainTableAlias, SelectSpec selectSpec) {
        return createSelectSqlTemplate(tableInfo, mainTableAlias, "COUNT(*)", selectSpec);
    }

    private String createSelectSqlTemplate(TableInfo<S> tableInfo, String mainTableAlias, String columnReplacer, SelectSpec selectSpec) {
        String createSelectSqlTemplate = createSelectSubSqlTemplate(tableInfo, "S", selectSpec);
        String selectSql = "SELECT " + columnReplacer + " FROM (" + createSelectSqlTemplate + ") " + mainTableAlias;

        if (selectSpec != null) {
            if (selectSpec.limit() != null && selectSpec.limit() > 0) {
                selectSql += " LIMIT " + selectSpec.limit();
            }
            if (selectSpec.offset() != null && selectSpec.offset() > 0) {
                selectSql += " OFFSET " + selectSpec.offset();
            }
        }
        return selectSql;
    }

    private String createSelectSubSqlTemplate(TableInfo<S> tableInfo, String tableAlias, SelectSpec selectSpec) {
        String tableName = tableInfo.tableFullName();

        String selectSql = "SELECT " + tableAlias + ".* FROM " + tableName + " " + tableAlias;
        if (selectSpec != null) {
            if (selectSpec.whereClause() != null && !selectSpec.whereClause().isBlank()) {
                selectSql += " WHERE " + selectSpec.whereClause();
            }
            if (selectSpec.orderByClause() != null && !selectSpec.orderByClause().isBlank()) {
                selectSql += " ORDER BY " + selectSpec.orderByClause();
            }
        }
        return selectSql;
    }
}