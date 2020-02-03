package com.andrewgilmartin.common.query;

/**
 * See Query
 */
public class PhraseQuery extends CompoundTermQuery {

    public PhraseQuery(float weight, String field, String... terms) {
        super(weight, field);
        for (String term : terms) {
            if (term != null) {
                addTerm(term);
            }
        }
    }

    public PhraseQuery(String field, String... terms) {
        super(field);
        for (String term : terms) {
            if (term != null) {
                addTerm(term);
            }
        }
    }
}
