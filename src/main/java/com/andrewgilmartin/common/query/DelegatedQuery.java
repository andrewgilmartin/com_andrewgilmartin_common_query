package com.andrewgilmartin.common.query;

/**
 * See Query
 */
public class DelegatedQuery extends Query {

    private final Query query;

    public DelegatedQuery(Query query) {
        this.query = query;
    }

    @Override
    public void setWeight(float weight) {
        query.setWeight(weight);
    }

    @Override
    public float getWeight() {
        return query.getWeight();
    }

    public Query getQuery() {
        return query;
    }
}
