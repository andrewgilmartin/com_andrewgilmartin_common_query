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

public abstract class QueryVisitor<RESULT,DATA> {

    public RESULT visitQuery(Query query) {
        return visit(query, null);
    }

    protected RESULT visit(Query query, DATA data) {
        if (query == null) {
            return null;
        }
        if (query instanceof PhraseQuery) {
            return visit((PhraseQuery) query, data);
        }
        if (query instanceof TermQuery) {
            return visit((TermQuery) query, data);
        }
        if (query instanceof VerbatimQuery) {
            return visit((VerbatimQuery) query, data);
        }
        if (query instanceof NumberQuery) {
            return visit((NumberQuery) query, data);
        }
        if (query instanceof BooleanQuery) {
            return visit((BooleanQuery) query, data);
        }
        if (query instanceof LuceneQuery) {
            return visit((LuceneQuery) query, data);
        }
        if (query instanceof AndQuery) {
            return visit((AndQuery) query, data);
        }
        if (query instanceof OrQuery) {
            return visit((OrQuery) query, data);
        }
        if (query instanceof NotQuery) {
            return visit((NotQuery) query, data);
        }
        throw new IllegalStateException("unknown query subclass");
    }

    protected abstract RESULT visit(TermQuery query, DATA data);

    protected abstract RESULT visit(VerbatimQuery query, DATA data);

    protected abstract RESULT visit(PhraseQuery query, DATA data);

    protected abstract RESULT visit(NumberQuery query, DATA data);

    protected abstract RESULT visit(BooleanQuery query, DATA data);

    protected abstract RESULT visit(LuceneQuery query, DATA data);

    protected abstract RESULT visit(AndQuery query, DATA data);

    protected abstract RESULT visit(OrQuery query, DATA data);

    protected abstract RESULT visit(NotQuery query, DATA data);
}
