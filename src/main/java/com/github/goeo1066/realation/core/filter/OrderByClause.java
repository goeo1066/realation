package com.github.goeo1066.realation.core.filter;

public class OrderByClause {
    private final WhereClauseBuilder builder;

    public OrderByClause(WhereClauseBuilder builder) {
        this.builder = builder;
    }

    public OrderByClause asc(Object column) {
        builder.addOrderBy(builder.getColumnHolder().getOneAndRemove(), "ASC");
        return this;
    }

    public OrderByClause desc(Object column) {
        builder.addOrderBy(builder.getColumnHolder().getOneAndRemove(), "DESC");
        return this;
    }
}
