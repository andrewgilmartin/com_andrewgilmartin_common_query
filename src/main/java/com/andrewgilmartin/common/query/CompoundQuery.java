package com.andrewgilmartin.common.query;

import java.util.ArrayList;
import java.util.List;

/**
 * See Query
 */
public class CompoundQuery extends Query {

    private final List<Query> queries = new ArrayList<>();

    protected CompoundQuery(float weight) {
        super(weight);
    }

    protected CompoundQuery() {
        // empty
    }

    public final void addQuery(Query query) {
        if (query == null) {
            throw new IllegalArgumentException("query is null");
        }
        queries.add(query);
    }

    public List<Query> getQueries() {
        return queries;
    }

    public boolean hasQueries() {
        return !queries.isEmpty();
    }
}
