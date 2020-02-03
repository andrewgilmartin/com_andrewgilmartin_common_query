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
import org.apache.lucene.search.BoostQuery;

public class LuceneQueryVisitor extends QueryVisitor {

    public org.apache.lucene.search.Query visit(Query query) {
        return (org.apache.lucene.search.Query) visit(query, null);
    }

    @Override
    protected Object visit(TermQuery query, Object data) {
        org.apache.lucene.index.Term t = new org.apache.lucene.index.Term(query.getField(), query.getTerm());
        org.apache.lucene.search.TermQuery tq = new org.apache.lucene.search.TermQuery(t);
        return boost(query, tq);
    }

    @Override
    protected Object visit(NumberQuery query, Object data) {
        org.apache.lucene.index.Term t = new org.apache.lucene.index.Term(query.getField(), query.getNumber().toString());
        org.apache.lucene.search.TermQuery tq = new org.apache.lucene.search.TermQuery(t);
        return boost(query, tq);
    }

    @Override
    protected Object visit(VerbatimQuery query, Object data) {
        org.apache.lucene.index.Term t = new org.apache.lucene.index.Term(query.getField(), query.getTerm());
        org.apache.lucene.search.TermQuery tq = new org.apache.lucene.search.TermQuery(t);
        return boost(query, tq);
    }

    @Override
    protected Object visit(BooleanQuery query, Object data) {
        org.apache.lucene.index.Term t = new org.apache.lucene.index.Term(query.getField(), Boolean.toString(query.getBoolean()));
        org.apache.lucene.search.TermQuery tq = new org.apache.lucene.search.TermQuery(t);
        return boost(query, tq);
    }

    @Override
    protected Object visit(PhraseQuery query, Object data) {
        org.apache.lucene.search.PhraseQuery.Builder builder = new org.apache.lucene.search.PhraseQuery.Builder();
        for (String term : query.getTerms()) {
            org.apache.lucene.index.Term t = new org.apache.lucene.index.Term(query.getField(), term);
            builder.add(t);
        }
        return boost(query, builder.build());
    }

    @Override
    protected Object visit(AndQuery query, Object data) {
        org.apache.lucene.search.BooleanQuery.Builder builder = new org.apache.lucene.search.BooleanQuery.Builder();
        for (Query q : query.getQueries()) {
            org.apache.lucene.search.Query luceneQuery = (org.apache.lucene.search.Query) visit(q, data);
            builder.add(new org.apache.lucene.search.BooleanClause(luceneQuery, org.apache.lucene.search.BooleanClause.Occur.MUST));
        }
        return boost(query, builder.build());
    }

    @Override
    protected Object visit(OrQuery query, Object data) {
        org.apache.lucene.search.BooleanQuery.Builder builder = new org.apache.lucene.search.BooleanQuery.Builder();
        for (Query q : query.getQueries()) {
            org.apache.lucene.search.Query luceneQuery = (org.apache.lucene.search.Query) visit(q, data);
            builder.add(new org.apache.lucene.search.BooleanClause(luceneQuery, org.apache.lucene.search.BooleanClause.Occur.SHOULD));
        }
        return boost(query, builder.build());
    }

    @Override
    protected Object visit(NotQuery query, Object data) {
        org.apache.lucene.search.BooleanQuery.Builder builder = new org.apache.lucene.search.BooleanQuery.Builder();
        for (Query q : query.getQueries()) {
            org.apache.lucene.search.Query luceneQuery = (org.apache.lucene.search.Query) visit(q, data);
            builder.add(new org.apache.lucene.search.BooleanClause(luceneQuery, org.apache.lucene.search.BooleanClause.Occur.MUST_NOT));
        }
        return boost(query, builder.build());
    }

    @Override
    protected Object visit(LuceneQuery query, Object data) {
        org.apache.lucene.search.Query q = query.getLuceneQuery();
        return boost(query, q);
    }

    private org.apache.lucene.search.Query boost(Query query, org.apache.lucene.search.Query luceneQuery) {
        return query.hasWeight() ? new BoostQuery(luceneQuery, query.getWeight()) : luceneQuery;
    }
}
