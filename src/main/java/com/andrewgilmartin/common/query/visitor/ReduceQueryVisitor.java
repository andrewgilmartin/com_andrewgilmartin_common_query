package com.andrewgilmartin.common.query.visitor;

import com.andrewgilmartin.common.query.AndQuery;
import com.andrewgilmartin.common.query.BooleanQuery;
import com.andrewgilmartin.common.query.CompoundQuery;
import com.andrewgilmartin.common.query.LuceneQuery;
import com.andrewgilmartin.common.query.NotQuery;
import com.andrewgilmartin.common.query.NumberQuery;
import com.andrewgilmartin.common.query.OrQuery;
import com.andrewgilmartin.common.query.PhraseQuery;
import com.andrewgilmartin.common.query.Query;
import com.andrewgilmartin.common.query.TermQuery;
import com.andrewgilmartin.common.query.VerbatimQuery;

/**
 * Reduce the query by eliminating empty queries.
 */
public class ReduceQueryVisitor extends QueryVisitor<Query, Void> {

    private boolean combineWeights = true;

    /**
     * If an and-query or an or-query is reduced to a single sub-query should
     * the sub-query's weight be combined with the eliminated compound query's
     * weight? The default is to combine the weights. Override combineWeights()
     * to define a combination.
     */
    public void setCombineWeights(boolean combineWeights) {
        this.combineWeights = combineWeights;
    }

    /**
     * Eliminate query if term is null.
     */
    @Override
    protected Query visit(TermQuery query, Void data) {
        return query.getTerm() != null ? query : null;
    }

    /**
     * Eliminate query if term is null.
     */
    @Override
    protected Query visit(VerbatimQuery query, Void data) {
        return query.getTerm() != null ? query : null;
    }

    /**
     * Eliminate query if it has no terms or all terms are null.
     */
    @Override
    protected Query visit(PhraseQuery query, Void data) {
        PhraseQuery reducedQuery = new PhraseQuery(query.getWeight(), query.getField());
        for (String term : query.getTerms()) {
            if (term != null) {
                reducedQuery.addTerm(term);
            }
        }
        return reducedQuery.hasTerms() ? reducedQuery : null;
    }

    /**
     * Eliminate query if number is null or is not finite.
     */
    @Override
    protected Query visit(NumberQuery query, Void data) {
        if (null == query.getNumber()) {
            return null;
        }
        if (Float.class == query.getNumber().getClass()) {
            return Float.isFinite(query.getNumber().floatValue()) ? query : null;
        }
        if (Double.class == query.getNumber().getClass()) {
            return Double.isFinite(query.getNumber().doubleValue()) ? query : null;
        }
        return query;
    }

    /**
     * Can't be reduced.
     */
    @Override
    protected Query visit(BooleanQuery query, Void data) {
        // can't be reduced
        return query;
    }

    /**
     * Eliminate query if internal Lucene query is null.
     */
    @Override
    protected Query visit(LuceneQuery query, Void data) {
        return query.getLuceneQuery() != null ? query : null;
    }

    /**
     * Eliminate query if it has no non-null sub-queries. If it has only one
     * sub-query then reduce to the one sub-query.
     */
    @Override
    protected Query visit(AndQuery originalQuery, Void data) {
        CompoundQuery reducedQuery = new AndQuery(originalQuery.getWeight());
        for (Query subquery : originalQuery.getQueries()) {
            Query reducedSubquery = (Query) visit(subquery, data);
            if (reducedSubquery != null) {
                reducedQuery.addQuery(reducedSubquery);
            }
        }
        switch (reducedQuery.getQueries().size()) {
            case 0:
                return null;
            case 1:
                return combineWeights(reducedQuery.getQueries().get(0), originalQuery);
            default:
                return reducedQuery;
        }
    }

    /**
     * Eliminate query if it has no non-null sub-queries. If it has only one
     * sub-query then reduce to the one sub-query.
     */
    @Override
    protected Query visit(OrQuery originalQuery, Void data) {
        CompoundQuery reducedQuery = new OrQuery(originalQuery.getWeight());
        for (Query subquery : originalQuery.getQueries()) {
            Query reducedSubquery = (Query) visit(subquery, data);
            if (reducedSubquery != null) {
                reducedQuery.addQuery(reducedSubquery);
            }
        }
        switch (reducedQuery.getQueries().size()) {
            case 0:
                return null;
            case 1:
                return combineWeights(reducedQuery.getQueries().get(0), originalQuery);
            default:
                return reducedQuery;
        }
    }

    /**
     * Eliminate query if has no non-null sub-queries.
     */
    @Override
    protected Query visit(NotQuery originalQuery, Void data) {
        CompoundQuery reducedQuery = new NotQuery(originalQuery.getWeight());
        for (Query subquery : originalQuery.getQueries()) {
            Query reducedSubquery = (Query) visit(subquery, data);
            if (reducedSubquery != null) {
                reducedQuery.addQuery(reducedSubquery);
            }
        }
        switch (reducedQuery.getQueries().size()) {
            case 0:
                return null;
            default:
                return reducedQuery;
        }
    }

    protected Query combineWeights(Query subQuery, Query compoundQuery) {
        Float weight = combineWeights(subQuery.getWeight(), compoundQuery.getWeight());
        if (weight != null) {
            subQuery.setWeight(weight);
        }
        return subQuery;
    }

    /**
     * Returns the combined weights or null if there is no combination. The
     * default combination is to multiply the weights.
     */
    protected Float combineWeights(float subQueryWeight, float compoundQueryWeight) {
        return combineWeights ? compoundQueryWeight * subQueryWeight : null;
    }
}

// END
