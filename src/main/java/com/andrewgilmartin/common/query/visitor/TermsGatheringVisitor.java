package com.andrewgilmartin.common.query.visitor;

import java.util.HashSet;
import java.util.Set;
import com.andrewgilmartin.common.query.BooleanQuery;
import com.andrewgilmartin.common.query.LuceneQuery;
import com.andrewgilmartin.common.query.PhraseQuery;
import com.andrewgilmartin.common.query.Query;
import com.andrewgilmartin.common.query.TermQuery;
import com.andrewgilmartin.common.query.VerbatimQuery;
import com.andrewgilmartin.common.query.AndQuery;
import com.andrewgilmartin.common.query.NotQuery;
import com.andrewgilmartin.common.query.NumberQuery;
import com.andrewgilmartin.common.query.OrQuery;

/**
 * Gather all the terms used in the query. Terms are found in TermQuery,
 * PhraseQuery, and VerbatimQuery instances. The default is to include all
 * fields and to ignore terms found within prohibited boolean clauses. Note
 * that a terms in a query within a NOT boolean clause within a NOT boolean 
 * clause is are gathered, ie a NOT negates a NOT.
 */
public class TermsGatheringVisitor extends QueryVisitor<Set<String>> {

    private Set<String> fieldsToInclude; // default is to include all
    private Set<String> fieldsToExclude; // default is to exclude none
    private boolean includeProhibitedTerms = false;
    private boolean vistingWithinProhibitedQuery = false;

    /**
     * Returns a set of gathered terms.
     */
    public static Set<String> getTerms(Query query) {
        TermsGatheringVisitor visitor = new TermsGatheringVisitor();
        return visitor.visit(query, new HashSet<>());
    }

    /**
     * Should gathered terms include those found in prohibited clauses?
     */
    public TermsGatheringVisitor setIncludeProhibitedTerms(boolean includeProhibitedTerms) {
        this.includeProhibitedTerms = includeProhibitedTerms;
        return this;
    }

    /**
     * Restrict gathered terms to this field (and others added later).
     */
    public TermsGatheringVisitor addIncludeField(String fieldName) {
        if (fieldName == null) {
            throw new IllegalArgumentException("fieldName must not be null");
        }
        if (fieldsToInclude == null) {
            fieldsToInclude = new HashSet();
        }
        fieldsToInclude.add(fieldName);
        return this;
    }

    /**
     * Avoid gathered terms from this field (and others added later).
     */
    public TermsGatheringVisitor addExcludeField(String fieldName) {
        if (fieldName == null) {
            throw new IllegalArgumentException("fieldName must not be null");
        }
        if (fieldsToExclude == null) {
            fieldsToExclude = new HashSet();
        }
        fieldsToExclude.add(fieldName);
        return this;
    }

    private boolean isFieldIncluded(String fieldName) {
        boolean include = fieldsToInclude == null || fieldsToInclude.contains(fieldName);
        boolean exclude = fieldsToExclude != null && fieldsToExclude.contains(fieldName);
        return include && !exclude;
    }

    private boolean isQueryIncluded(String fieldName) {
        return (includeProhibitedTerms || !vistingWithinProhibitedQuery) && isFieldIncluded(fieldName);
    }

    @Override
    protected Set<String> visit(TermQuery query, Set<String> allTerms) {
        if (isQueryIncluded(query.getField())) {
            allTerms.add(query.getTerm());
        }
        return allTerms;
    }

    @Override
    protected Set<String> visit(NumberQuery query, Set<String> allTerms) {
        if (isQueryIncluded(query.getField())) {
            allTerms.add(query.getNumber().toString());
        }
        return allTerms;
    }

    @Override
    protected Set<String> visit(VerbatimQuery query, Set<String> allTerms) {
        if (isQueryIncluded(query.getField())) {
            allTerms.add(query.getTerm());
        }
        return allTerms;
    }

    @Override
    protected Set<String> visit(PhraseQuery query, Set<String> allTerms) {
        if (isQueryIncluded(query.getField())) {
            for (String term : query.getTerms()) {
                allTerms.add(term);
            }
        }
        return allTerms;
    }

    @Override
    protected Set<String> visit(BooleanQuery query, Set<String> allTerms) {
        if (isQueryIncluded(query.getField())) {
            allTerms.add(Boolean.toString(query.getBoolean()));
        }
        return allTerms;
    }

    @Override
    protected Set<String> visit(AndQuery query, Set<String> allTerms) {
        for (Query subQuery : query.getQueries()) {
            visit(subQuery, allTerms);
        }
        return allTerms;
    }

    @Override
    protected Set<String> visit(OrQuery query, Set<String> allTerms) {
        for (Query subQuery : query.getQueries()) {
            visit(subQuery, allTerms);
        }
        return allTerms;
    }

    @Override
    protected Set<String> visit(NotQuery query, Set<String> allTerms) {
        vistingWithinProhibitedQuery = !vistingWithinProhibitedQuery;
        try {
            for (Query subQuery : query.getQueries()) {
                visit(subQuery, allTerms);
            }
            return allTerms;
        } finally {
            vistingWithinProhibitedQuery = !vistingWithinProhibitedQuery;
        }
    }

    @Override
    protected Set<String> visit(LuceneQuery query, Set<String> allTerms) {
        return allTerms;
    }
}

// END
