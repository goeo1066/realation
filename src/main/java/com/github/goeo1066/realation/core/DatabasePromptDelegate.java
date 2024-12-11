package com.github.goeo1066.realation.core;

import com.github.goeo1066.realation.core.filter.WhereClauseBuilder;
import com.github.goeo1066.realation.select.SelectSpec;
import com.github.goeo1066.realation.select.SelectSqlComposer;
import com.github.goeo1066.realation.select.SelectSqlComposerPostgreSQL;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

public class DatabasePromptDelegate<S> {
    private final SelectSqlComposer<S> selectSqlComposer;
    private final TableInfo<S> tableInfo;

    public DatabasePromptDelegate(
            TableInfo<S> tableInfo
    ) {
        this.selectSqlComposer = new SelectSqlComposerPostgreSQL<>();
        this.tableInfo = tableInfo;
    }

    public List<S> select(NamedParameterJdbcTemplate jdbcTemplate, SelectSpec selectSpec) {
        String selectSql = selectSqlComposer.composeSelectSql(tableInfo, selectSpec);
        try (var stream = jdbcTemplate.queryForStream(selectSql, Map.of(), tableInfo.getRowMapper())) {
            return stream.toList();
        }
    }

    public List<S> select(NamedParameterJdbcTemplate jdbcTemplate, WhereClauseBuilder builder) throws SQLException {
        String selectSql = selectSqlComposer.composeSelectSql(tableInfo, builder);
        JdbcTemplate original = jdbcTemplate.getJdbcTemplate();

        try (var stream = original.queryForStream(selectSql, tableInfo.getRowMapper(), builder.getPlaceholdersAsArray())) {
            return stream.toList();
        }
    }
}
