package com.andrewgilmartin.common.query;

public class LuceneQuery extends Query {

    private org.apache.lucene.search.Query luceneQuery;

    public LuceneQuery( float weight, org.apache.lucene.search.Query luceneQuery ) {
        super(weight);
        this.luceneQuery = luceneQuery;
    }

    public LuceneQuery( org.apache.lucene.search.Query luceneQuery ) {
        this.luceneQuery = luceneQuery;
    }

    public org.apache.lucene.search.Query getLuceneQuery() {
        return luceneQuery;
    }
}

// END