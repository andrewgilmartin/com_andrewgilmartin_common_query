package com.andrewgilmartin.common.query.visitor;

import com.andrewgilmartin.common.query.OrQuery;
import com.andrewgilmartin.common.query.PhraseQuery;
import com.andrewgilmartin.common.query.Query;
import com.andrewgilmartin.common.query.QueryException;
import com.andrewgilmartin.common.query.TermQuery;

/**
 * Replace hyphenated terms with a boolean query of the term and a phrase of the terms.
 * For example,
 *
 * <blockquote>
 *      intelligent-enterprise
 * </blockquote>
 *
 * is replaced with
 *
 * <blockquote>
 *      intelligent-enterprise OR "intelligent enterprise"
 * </blockquote>
 *
 * The weight of the phrase query is weight of the original term query multiplied by
 * the weight given at construction. For example, if the term weight is 5 and the
 * multiplier is 0.5 then the phrase weight is 2.5.
 */
public class HyphenatedTermQueryVisitor extends QueryVisitorAdaptor {

    private final float weight;

    public HyphenatedTermQueryVisitor( float weight ) {
        this.weight = weight;
    }

    public Query visit( Query query ) throws QueryException {
        return visit( query, null );
    }

    @Override
    protected Query visit( TermQuery termQuery, Query data ) throws QueryException {
        if (!termQuery.getTerm().contains("-")) return termQuery;

        PhraseQuery phraseQuery = new PhraseQuery( termQuery.getField() );
        String[] terms = termQuery.getTerm().split("-");
        for (String term : terms) {
            phraseQuery.addTerm(term);
        }
        phraseQuery.setWeight( termQuery.getWeight() * weight );

        OrQuery bq = new OrQuery();
        bq.addQuery( termQuery );
        bq.addQuery( phraseQuery );

        return bq;
    }

}

// END