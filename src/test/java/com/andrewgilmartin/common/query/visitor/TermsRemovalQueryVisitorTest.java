package com.andrewgilmartin.common.query.visitor;

import com.andrewgilmartin.common.query.AndQuery;
import com.andrewgilmartin.common.query.NotQuery;
import com.andrewgilmartin.common.query.PhraseQuery;
import com.andrewgilmartin.common.query.Query;
import com.andrewgilmartin.common.query.TermQuery;
import java.util.Arrays;
import static org.junit.Assert.assertEquals;
import org.junit.Before;
import org.junit.Test;

public class TermsRemovalQueryVisitorTest {

    Query query;
    String queryDebug;

    @Before
    public void setUp() {
        query = new AndQuery(Float.NaN,
                new TermQuery(Float.NaN, "x", "a"),
                new TermQuery(Float.NaN, "y", "b"),
                new PhraseQuery(Float.NaN, "x", "a", "b"),
                new PhraseQuery(Float.NaN, "y", "c", "d")
        );
        queryDebug = toString(query);
    }

    @Test
    public void testSameSet() {
        TermsRemovalQueryVisitor vistor = new TermsRemovalQueryVisitor(Arrays.asList("a", "d"));
        Query result = vistor.visit(query);
        assertEquals("( y: \"b\" AND  x: \"b\" AND  y: \"c\")", toString(result));
    }

    @Test
    public void testDifferentSets() {
        TermsRemovalQueryVisitor vistor = new TermsRemovalQueryVisitor(
                Arrays.asList("a"), // remove from TermQuery
                Arrays.asList("d") // remove from PhraseQuery
        );
        Query result = vistor.visit(query);
        assertEquals("( y: \"b\" AND  x: \"a b\" AND  y: \"c\")", toString(result));
    }

    private static String toString(Query query) {
        return new SolrLuceneQueryVistor().visitQuery(query).toString();
    }
}
