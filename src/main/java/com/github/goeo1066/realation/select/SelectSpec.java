package com.github.goeo1066.realation.select;


public record SelectSpec(String whereClause, String orderByClause, Long limit, Long offset) {

    public static Builder builder() {
        return new Builder();
    }

    public SelectSpec withoutPaging() {
        return new SelectSpec(whereClause, orderByClause, null, null);
    }

    public static class Builder {
        private String whereClause;
        private String orderByClause;
        private Long limit;
        private Long offset;

        public Builder whereClause(String whereClause) {
            this.whereClause = whereClause;
            return this;
        }

        public Builder orderByClause(String orderByClause) {
            this.orderByClause = orderByClause;
            return this;
        }

        public Builder limit(Long limit) {
            this.limit = limit;
            return this;
        }

        public Builder limit(Integer limit) {
            this.limit = limit == null ? null : limit.longValue();
            return this;
        }

        public Builder offset(Long offset) {
            return this;
        }

        public Builder offset(Integer offset) {
            this.offset = offset == null ? null : offset.longValue();
            return this;
        }

        public SelectSpec build() {
            return new SelectSpec(whereClause, orderByClause, limit, offset);
        }
    }
}
