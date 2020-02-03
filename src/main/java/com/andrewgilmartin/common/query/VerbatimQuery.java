package com.andrewgilmartin.common.query;

/**
 * See Query
 */
public class VerbatimQuery extends Query {

    private final String field;
    private final String term;

    public VerbatimQuery(float weight, String field, String term) {
        super(weight);
        this.field = field;
        this.term = term;
    }

    public VerbatimQuery(String field, String term) {
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


// END
