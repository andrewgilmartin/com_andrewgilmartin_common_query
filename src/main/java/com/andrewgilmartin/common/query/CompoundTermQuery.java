package com.andrewgilmartin.common.query;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * See Query
 */
public class CompoundTermQuery extends Query {

    private final String field;
    private final List<String> terms = new ArrayList<>();

    protected CompoundTermQuery(float weight, String field) {
        super(weight);
        this.field = field;
    }

    protected CompoundTermQuery(String field) {
        this.field = field;
    }

    public String getField() {
        return field;
    }

    public Collection<String> getTerms() {
        return terms;
    }

    public final void addTerm(String term) {
        if (term == null) {
            throw new IllegalArgumentException("term is null");
        }
        terms.add(term);
    }

    public boolean hasTerms() {
        return !terms.isEmpty();
    }
}
