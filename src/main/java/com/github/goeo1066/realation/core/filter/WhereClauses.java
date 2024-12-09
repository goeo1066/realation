package com.github.goeo1066.realation.core.filter;

import java.util.Arrays;

public class WhereClauses {
    public WhereClauses eq(Object ignored, Object value) {
        System.out.println("eq: " + value);
        return this;
    }

    public WhereClauses in(Object ignored, Object... values) {
        System.out.println("in: " + Arrays.toString(values));
        return this;
    }

    public WhereClauses and() {
        System.out.println("and ");
        return this;
    }

    public WhereClauses or() {
        System.out.println("or ");
        return this;
    }
}
