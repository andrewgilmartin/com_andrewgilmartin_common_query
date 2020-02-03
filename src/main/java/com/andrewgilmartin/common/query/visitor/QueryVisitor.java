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

public abstract class QueryVisitor<T> {

    public T visitQuery( Query query ) throws QueryException {
        return visit( query, null);
    }

    protected T visit( Query query, T data ) throws QueryException {
        if ( query == null ) {
            return null;
        }
        if ( query instanceof PhraseQuery ) {
            return visit( (PhraseQuery) query, data );
        }
        if ( query instanceof TermQuery ) {
            return visit( (TermQuery) query, data );
        }
        if ( query instanceof VerbatimQuery ) {
            return visit( (VerbatimQuery) query, data );
        }
        if ( query instanceof NumberQuery ) {
            return visit( (NumberQuery) query, data );
        }
        if ( query instanceof BooleanQuery ) {
            return visit( (BooleanQuery) query, data );
        }
        if ( query instanceof LuceneQuery ) {
            return visit( (LuceneQuery) query, data );
        }
        if ( query instanceof AndQuery ) {
            return visit( (AndQuery) query, data );
        }
        if ( query instanceof OrQuery ) {
            return visit( (OrQuery) query, data );
        }
        if ( query instanceof NotQuery ) {
            return visit( (NotQuery) query, data );
        }
        throw new QueryException( "unknown query subclass" );
    }

    protected abstract T visit( TermQuery query, T data ) throws QueryException;
    protected abstract T visit( VerbatimQuery query, T data ) throws QueryException;
    protected abstract T visit( PhraseQuery query, T data ) throws QueryException;
    protected abstract T visit( NumberQuery query, T data ) throws QueryException;
    protected abstract T visit( BooleanQuery query, T data ) throws QueryException;
    protected abstract T visit( LuceneQuery query, T data ) throws QueryException;
    protected abstract T visit( AndQuery query, T data ) throws QueryException;
    protected abstract T visit( OrQuery query, T data ) throws QueryException;
    protected abstract T visit( NotQuery query, T data ) throws QueryException;
}

