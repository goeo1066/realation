package com.github.goeo1066.realation.core.filter;

import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class WhereClauses {
    private final WhereClauseBuilder builder;

    public WhereClauses(WhereClauseBuilder builder) {
        this.builder = builder;
    }

    public WhereClauses eq(Object ignored, Object value) {
        builder.getStringBuilder().append(builder.getColumnHolder().getOneAndRemove()).append(" = ?");
        builder.getPlaceholders().add(value);
        builder.getPlaceholderTypes().add(value.getClass());
        return this;
    }

    public WhereClauses ne(Object ignored, Object value) {
        builder.getStringBuilder().append(builder.getColumnHolder().getOneAndRemove()).append(" <> ?");
        builder.getPlaceholders().add(value);
        builder.getPlaceholderTypes().add(value.getClass());
        return this;
    }

    public WhereClauses in(Object ignored, Object... values) {
        builder.getStringBuilder().append(builder.getColumnHolder().getOneAndRemove()).append(" IN (");

        String placeholders = IntStream.range(0, values.length).mapToObj(it -> " ? ").collect(Collectors.joining(", "));
        builder.getStringBuilder().append(placeholders);

        for (Object value : values) {
            builder.getPlaceholders().add(value);
            builder.getPlaceholderTypes().add(value.getClass());
        }

        builder.getStringBuilder().append(')');
        return this;
    }

    public WhereClauses isNull(Object ignored) {
        builder.getStringBuilder().append(builder.getColumnHolder().getOneAndRemove()).append(" IS NULL ");
        return this;
    }

    public WhereClauses isNotNull(Object ignored) {
        builder.getStringBuilder().append(builder.getColumnHolder().getOneAndRemove()).append(" IS NOT NULL ");
        return this;
    }

    public WhereClauses and() {
        builder.getStringBuilder().append(" AND ");
        return this;
    }

    public WhereClauses or() {
        builder.getStringBuilder().append(" OR ");
//        System.out.println("or ");
        return this;
    }

    public OrderByClause orderBy() {
        return new OrderByClause(builder);
    }
}
