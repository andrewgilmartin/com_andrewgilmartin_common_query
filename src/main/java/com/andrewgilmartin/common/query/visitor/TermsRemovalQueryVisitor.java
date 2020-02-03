package com.andrewgilmartin.common.query.visitor;

import java.util.Collection;
import com.andrewgilmartin.common.query.PhraseQuery;
import com.andrewgilmartin.common.query.Query;
import com.andrewgilmartin.common.query.TermQuery;
import java.util.HashSet;
import java.util.Set;

/**
 * Removes a set of terms from term-queries and from phrase-queries. Different
 * term lists can be used for each query type, if wanted.
 */
public class TermsRemovalQueryVisitor extends QueryVisitorAdaptor {

    private final Set<String> termsToRemove = new HashSet<>();
    private final Set<String> phraseTermsToRemove = new HashSet<>();

    public TermsRemovalQueryVisitor(Iterable<String> termsToRemove, Iterable<String> phraseTermsToRemove) {
        if (termsToRemove != null) {
            for (String term : termsToRemove) {
                this.termsToRemove.add(term);
            }
        }
        if (phraseTermsToRemove != null) {
            for (String phraseTerm : phraseTermsToRemove) {
                this.phraseTermsToRemove.add(phraseTerm);
            }
        }
    }

    public TermsRemovalQueryVisitor(Collection<String> terms) {
        this(terms, terms);
    }

    public Query visit(Query query) {
        return visit(query, null);
    }

    @Override
    protected Query visit(TermQuery query, Query data) {
        return termsToRemove.contains(query.getTerm()) ? null : query;
    }

    @Override
    protected Query visit(PhraseQuery query, Query data) {
        boolean phraseChanged = false;
        PhraseQuery pq = new PhraseQuery(query.getField());
        for (String term : query.getTerms()) {
            if (!phraseTermsToRemove.contains(term)) {
                pq.addTerm(term);
            } else {
                phraseChanged = true;
            }
        }
        pq.setWeight(query.getWeight());
        return phraseChanged ? (pq.hasTerms() /* ie, not empty */ ? pq : null) : query;
    }
}

// END
