package com.andrewgilmartin.common.query.visitor;

import com.andrewgilmartin.common.query.AndQuery;
import com.andrewgilmartin.common.query.BooleanQuery;
import com.andrewgilmartin.common.query.LuceneQuery;
import com.andrewgilmartin.common.query.NotQuery;
import com.andrewgilmartin.common.query.NumberQuery;
import com.andrewgilmartin.common.query.OrQuery;
import com.andrewgilmartin.common.query.PhraseQuery;
import com.andrewgilmartin.common.query.Query;
import com.andrewgilmartin.common.query.QueryException;
import com.andrewgilmartin.common.query.TermQuery;
import com.andrewgilmartin.common.query.VerbatimQuery;

/**
 * This class is suitable as the base for a vistor that needs to modify the
 * query.
 */
public class QueryVisitorAdaptor extends QueryVisitor<Query> {

    /**
     * Return null to drop the boolean query from its parent.
     */
    @Override
    protected Query visit(BooleanQuery query, Query data) throws QueryException {
        return query;
    }

    /**
     * Return null to drop the term query from its parent.
     */
    @Override
    protected Query visit(TermQuery query, Query data) throws QueryException {
        return query;
    }

    /**
     * Return null to drop the term query from its parent.
     */
    @Override
    protected Query visit(VerbatimQuery query, Query data) throws QueryException {
        return query;
    }

    /**
     * Return null to drop the phrase query from its parent.
     */
    @Override
    protected Query visit(PhraseQuery query, Query data) throws QueryException {
        return query;
    }

    @Override
    protected Query visit(NumberQuery query, Query data) throws QueryException {
        return query;
    }

    /**
     * Return null to drop the lucene query from its parent.
     */
    @Override
    protected Query visit(LuceneQuery query, Query data) throws QueryException {
        return query;
    }

    @Override
    protected Query visit(AndQuery query, Query data) throws QueryException {
        AndQuery qq = new AndQuery();
        for (Query q : query.getQueries()) {
            Query rq = visit(q, data);
            if (rq != null) {
                qq.addQuery(rq);
            }
        }
        qq.setWeight(query.getWeight());
        return qq.hasQueries() ? qq : null;
    }

    @Override
    protected Query visit(OrQuery query, Query data) throws QueryException {
        OrQuery qq = new OrQuery();
        for (Query q : query.getQueries()) {
            Query rq = visit(q, data);
            if (rq != null) {
                qq.addQuery(rq);
            }
        }
        qq.setWeight(query.getWeight());
        return qq.hasQueries() ? qq : null;
    }

    @Override
    protected Query visit(NotQuery query, Query data) throws QueryException {
        NotQuery qq = new NotQuery();
        for (Query q : query.getQueries()) {
            Query rq = visit(q, data);
            if (rq != null) {
                qq.addQuery(rq);
            }
        }
        qq.setWeight(query.getWeight());
        return qq.hasQueries() ? qq : null;
    }
}
