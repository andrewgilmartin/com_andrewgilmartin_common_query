package com.andrewgilmartin.common.query.visitor;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import com.andrewgilmartin.common.query.PhraseQuery;
import com.andrewgilmartin.common.query.Query;
import com.andrewgilmartin.common.query.QueryException;
import com.andrewgilmartin.common.query.TermQuery;

/**
 * Rewrites the given query so that TermQueries are replaced with a BooleanQuery
 * for the term or any of its equivalents. Hits on the original term can he
 * ranked differently than hits on an equivalent term.
 */
public class TermsRemovalQueryVisitor extends QueryVisitorAdaptor {

    private Map<String, String> terms;
    private Map<String, String> phraseTerms;

    public TermsRemovalQueryVisitor(String[] terms, String[] phraseTerms) {
        this.terms = new HashMap<String, String>();
        if (terms != null) {
            for (String term : terms) {
                this.terms.put(term, term);
            }
        }
        this.phraseTerms = new HashMap<String, String>();
        if (phraseTerms != null) {
            for (String phraseTerm : phraseTerms) {
                this.phraseTerms.put(phraseTerm, phraseTerm);
            }
        }
    }

    public TermsRemovalQueryVisitor(Collection<String> terms, Collection<String> phraseTerms) {
        this.terms = new HashMap<String, String>();
        if (terms != null) {
            for (String term : terms) {
                this.terms.put(term, term);
            }
        }
        this.phraseTerms = new HashMap<String, String>();
        if (phraseTerms != null) {
            for (String phraseTerm : phraseTerms) {
                this.phraseTerms.put(phraseTerm, phraseTerm);
            }
        }
    }

    public Query visit(Query query) throws QueryException {
        return visit(query, null);
    }

    @Override
    protected Query visit(TermQuery query, Query data) throws QueryException {
        return terms.containsKey(query.getTerm()) ? null : query;
    }

    @Override
    protected Query visit(PhraseQuery query, Query data) throws QueryException {
        boolean phraseChanged = false;
        PhraseQuery pq = new PhraseQuery(query.getField());
        for (String term : query.getTerms()) {
            if (!phraseTerms.containsKey(term)) {
                pq.addTerm(term);
            }
            else {
                phraseChanged = true;
            }
        }
        pq.setWeight(query.getWeight());
        return phraseChanged ? (pq.hasTerms() ? pq : null) : query;
    }
}

// END