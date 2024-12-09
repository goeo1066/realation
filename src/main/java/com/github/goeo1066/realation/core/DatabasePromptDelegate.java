package com.github.goeo1066.realation.core;

import com.github.goeo1066.realation.select.SelectSpec;
import com.github.goeo1066.realation.select.SelectSqlComposer;
import com.github.goeo1066.realation.select.SelectSqlComposerPostgreSQL;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

import java.util.List;
import java.util.Map;

public class DatabasePromptDelegate<S> {
    private final SelectSqlComposer<S> selectSqlComposer;
    private final TableInfo<S> tableInfo;

    public DatabasePromptDelegate(
            Class<S> entityClass
    ) throws NoSuchMethodException {
        this.selectSqlComposer = new SelectSqlComposerPostgreSQL<>();
        this.tableInfo = TableInfo.retrieveFromEntity(entityClass);
    }

    public List<S> select(NamedParameterJdbcTemplate jdbcTemplate, SelectSpec selectSpec) {
        String selectSql = selectSqlComposer.composeSelectSql(tableInfo, selectSpec);
        try (var stream = jdbcTemplate.queryForStream(selectSql, Map.of(), tableInfo.getRowMapper())) {
            return stream.toList();
        }
    }
}
