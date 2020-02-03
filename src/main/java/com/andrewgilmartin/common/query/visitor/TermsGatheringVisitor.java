package com.andrewgilmartin.common.query.visitor;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import com.andrewgilmartin.common.query.BooleanQuery;
import com.andrewgilmartin.common.query.LuceneQuery;
import com.andrewgilmartin.common.query.PhraseQuery;
import com.andrewgilmartin.common.query.Query;
import com.andrewgilmartin.common.query.QueryException;
import com.andrewgilmartin.common.query.TermQuery;
import com.andrewgilmartin.common.query.VerbatimQuery;
import com.andrewgilmartin.common.query.AndQuery;
import com.andrewgilmartin.common.query.NotQuery;
import com.andrewgilmartin.common.query.NumberQuery;
import com.andrewgilmartin.common.query.OrQuery;

/**
 * Gather all the terms used in the query. Terms are found in TermQuery,
 * PhraseQuery, and VerbatimQuery instances. The default is to ignore terms
 * found within prohibited boolean clauses.
 */
public class TermsGatheringVisitor extends QueryVisitor<Set<String>> {

    private Map<String, String> fieldsToInclude; // default is to include all
    private Map<String, String> fieldsToExclude; // default is to exclude none
    private boolean includeProhibitedTerms = false;
    private int vistingWithinProhibitedQuery = 0;

    /**
     * Returns an unordered collection of the found terms.
     */
    public static Set<String> getTerms(Query query) throws QueryException {
        try {
            TermsGatheringVisitor visitor = new TermsGatheringVisitor();
            return visitor.visit(query, new HashSet<String>());
        } catch (QueryException e) {
            throw new QueryException(e, "unable to gather terms from query: query={0}", query);
        }
    }

    /**
     * Should collected terms include those found in prohibited clauses?
     */
    public void setIncludeProhibitedTerms(boolean includeProhibitedTerms) {
        this.includeProhibitedTerms = includeProhibitedTerms;
    }

    public void addIncludeField(String fieldName) {
        if (fieldName == null) {
            throw new IllegalArgumentException("fieldName must not be null");
        }
        if (fieldsToInclude == null) {
            fieldsToInclude = new HashMap();
        }
        fieldsToInclude.put(fieldName, fieldName);
    }

    public void addExcludeField(String fieldName) {
        if (fieldName == null) {
            throw new IllegalArgumentException("fieldName must not be null");
        }
        if (fieldsToExclude == null) {
            fieldsToExclude = new HashMap();
        }
        fieldsToExclude.put(fieldName, fieldName);
    }

    private boolean isFieldIncluded(String fieldName) {
        boolean include = fieldsToInclude == null || fieldsToInclude.containsKey(fieldName);
        boolean exclude = fieldsToExclude != null && fieldsToExclude.containsKey(fieldName);
        return include && !exclude;
    }

    private boolean isQueryIncluded(String fieldName) {
        return (includeProhibitedTerms || vistingWithinProhibitedQuery == 0) && isFieldIncluded(fieldName);
    }

    @Override
    protected Set<String> visit(TermQuery query, Set<String> allTerms) throws QueryException {
        if (isQueryIncluded(query.getField())) {
            allTerms.add(query.getTerm());
        }
        return allTerms;
    }

    @Override
    protected Set<String> visit(NumberQuery query, Set<String> allTerms) throws QueryException {
        if (isQueryIncluded(query.getField())) {
            allTerms.add(query.getNumber().toString());
        }
        return allTerms;
    }

    @Override
    protected Set<String> visit(VerbatimQuery query, Set<String> allTerms) throws QueryException {
        if (isQueryIncluded(query.getField())) {
            allTerms.add(query.getTerm());
        }
        return allTerms;
    }

    @Override
    protected Set<String> visit(PhraseQuery query, Set<String> allTerms) throws QueryException {
        if (isQueryIncluded(query.getField())) {
            for (String term : query.getTerms()) {
                allTerms.add(term);
            }
        }
        return allTerms;
    }

    @Override
    protected Set<String> visit(BooleanQuery query, Set<String> allTerms) throws QueryException {
        if (isQueryIncluded(query.getField())) {
            allTerms.add(Boolean.toString(query.getBoolean()));
        }
        return allTerms;
    }

    @Override
    protected Set<String> visit(AndQuery query, Set<String> allTerms) throws QueryException {
        for (Query subQuery : query.getQueries()) {
            visit(subQuery, allTerms);
        }
        return allTerms;
    }

    @Override
    protected Set<String> visit(OrQuery query, Set<String> allTerms) throws QueryException {
        for (Query subQuery : query.getQueries()) {
            visit(subQuery, allTerms);
        }
        return allTerms;
    }

    @Override
    protected Set<String> visit(NotQuery query, Set<String> allTerms) throws QueryException {
        for (Query subQuery : query.getQueries()) {
            visit(subQuery, allTerms);
        }
        return allTerms;
    }

    @Override
    protected Set<String> visit(LuceneQuery query, Set<String> allTerms) throws QueryException {
        return allTerms;
    }
}

// END
