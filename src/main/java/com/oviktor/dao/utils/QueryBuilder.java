package com.oviktor.dao.utils;

public class QueryBuilder {

    private final StringBuilder query;
    private Sorting sorting = Sorting.ASC;
    private String filtering = "";

    public QueryBuilder(String firstPartOfQuery) {
        firstPartOfQuery = firstPartOfQuery.strip();
        if (firstPartOfQuery.endsWith(";")) {
            firstPartOfQuery = firstPartOfQuery.substring(0, firstPartOfQuery.length() - 1);
        }
        query = new StringBuilder(firstPartOfQuery);
    }

    public QueryBuilder withSorting(Sorting sorting) {
        this.sorting = sorting;
        return this;
    }

    public QueryBuilder withFiltering(String filtering) {
        this.filtering = filtering;
        return this;
    }

    public QueryBuilder withPagination(Pagination pagination) {
        this.query
                .append(" limit ")
                .append(pagination.getPageSize())
                .append(" offset ")
                .append(pagination.getPageSize() * (pagination.getPageNum() - 1));
        return this;
    }

    public String build() {
        return query.toString()
                .strip()
                .replace("$sorting", sorting.toString().toLowerCase())
                .replace("$filtering", filtering)
                .replaceAll("\\s+", " ");
    }
}
