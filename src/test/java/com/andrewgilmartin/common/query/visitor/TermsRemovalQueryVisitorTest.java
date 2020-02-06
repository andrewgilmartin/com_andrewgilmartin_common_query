package com.andrewgilmartin.common.query.visitor;

import com.andrewgilmartin.common.query.AndQuery;
import com.andrewgilmartin.common.query.PhraseQuery;
import com.andrewgilmartin.common.query.Query;
import com.andrewgilmartin.common.query.TermQuery;
import java.util.Arrays;
import static org.junit.Assert.assertEquals;
import org.junit.Before;
import org.junit.Test;

public class TermsRemovalQueryVisitorTest {

    private Query query;

    @Before
    public void setUp() {
        query = new AndQuery(
                new TermQuery("x", "a"),
                new TermQuery("y", "b"),
                new PhraseQuery("x", "a", "b"),
                new PhraseQuery("y", "c", "d")
        );
    }

    @Test
    public void testSameSet() {
        TermsRemovalQueryVisitor vistor = new TermsRemovalQueryVisitor(Arrays.asList("a", "d"));
        Query result = vistor.visit(query);
        assertEquals("(y: \"b\" AND x: \"b\" AND y: \"c\")", toString(result));
    }

    @Test
    public void testDifferentSets() {
        TermsRemovalQueryVisitor vistor = new TermsRemovalQueryVisitor(
                Arrays.asList("a"), // remove from TermQuery
                Arrays.asList("d") // remove from PhraseQuery
        );
        Query result = vistor.visit(query);
        assertEquals("(y: \"b\" AND x: \"a b\" AND y: \"c\")", toString(result));
    }

    private static String toString(Query query) {
        return new SolrLuceneQueryVistor().visitQuery(query).toString();
    }
}
