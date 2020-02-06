package com.andrewgilmartin.common.query.visitor;

import com.andrewgilmartin.common.query.AndQuery;
import com.andrewgilmartin.common.query.BooleanQuery;
import com.andrewgilmartin.common.query.LuceneQuery;
import com.andrewgilmartin.common.query.NotQuery;
import com.andrewgilmartin.common.query.NumberQuery;
import com.andrewgilmartin.common.query.OrQuery;
import com.andrewgilmartin.common.query.PhraseQuery;
import com.andrewgilmartin.common.query.Query;
import com.andrewgilmartin.common.query.TermQuery;
import com.andrewgilmartin.common.query.VerbatimQuery;

/**
 * Use this adaptor as the base for a visitor that will modify only a few query
 * subclasses.
 */
public class QueryVisitorAdaptor<DATA> extends QueryVisitor<Query, DATA> {

    /**
     * Return null to drop the boolean query from its parent.
     */
    @Override
    protected Query visit(BooleanQuery query, DATA data) {
        return query;
    }

    /**
     * Return null to drop the term query from its parent.
     */
    @Override
    protected Query visit(TermQuery query, DATA data) {
        return query;
    }

    /**
     * Return null to drop the term query from its parent.
     */
    @Override
    protected Query visit(VerbatimQuery query, DATA data) {
        return query;
    }

    /**
     * Return null to drop the phrase query from its parent.
     */
    @Override
    protected Query visit(PhraseQuery query, DATA data) {
        return query;
    }

    @Override
    protected Query visit(NumberQuery query, DATA data) {
        return query;
    }

    /**
     * Return null to drop the lucene query from its parent.
     */
    @Override
    protected Query visit(LuceneQuery query, DATA data) {
        return query;
    }

    @Override
    protected Query visit(AndQuery query, DATA data) {
        AndQuery qq = new AndQuery(query.getWeight());
        for (Query q : query.getQueries()) {
            Query rq = visit(q, data);
            if (rq != null) {
                qq.addQuery(rq);
            }
        }
        return qq;
    }

    @Override
    protected Query visit(OrQuery query, DATA data) {
        OrQuery qq = new OrQuery(query.getWeight());
        for (Query q : query.getQueries()) {
            Query rq = visit(q, data);
            if (rq != null) {
                qq.addQuery(rq);
            }
        }
        return qq;
    }

    @Override
    protected Query visit(NotQuery query, DATA data) {
        NotQuery qq = new NotQuery(query.getWeight());
        for (Query q : query.getQueries()) {
            Query rq = visit(q, data);
            if (rq != null) {
                qq.addQuery(rq);
            }
        }
        return qq;
    }
}
