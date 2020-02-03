package com.andrewgilmartin.common.query;

/**
 * See Query
 */
public class TermQuery extends Query {

    private final String field;
    private final String term;

    public TermQuery(float weight, String field, String term) {
        super(weight);
        this.field = field;
        this.term = term;
    }

    public TermQuery(String field, String term) {
        this.field = field;
        this.term = term;
    }

    public String getField() {
        return this.field;
    }

    public String getTerm() {
        return this.term;
    }
}
