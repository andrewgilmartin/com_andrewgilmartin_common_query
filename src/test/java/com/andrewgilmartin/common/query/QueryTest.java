package com.andrewgilmartin.common.query;

import com.andrewgilmartin.common.query.visitor.SolrLuceneQueryVistor;
import static org.junit.Assert.assertEquals;
import org.junit.Test;

public class QueryTest {

    @Test
    public void testAnd() {
        AndQuery q = new AndQuery();
        q.addQuery(new BooleanQuery("f", true));
        q.addQuery(new NumberQuery("f", 1));
        q.addQuery(new TermQuery("f", "t"));
        q.addQuery(new PhraseQuery("f", "p1", "p2"));
        q.addQuery(new VerbatimQuery("f", "v"));

        assertEquals("(f: true AND f: 1 AND f: \"t\" AND f: \"p1 p2\" AND f: \"v\")", toString(q));
    }

    @Test
    public void testOr() {
        OrQuery q = new OrQuery();
        q.addQuery(new BooleanQuery("f", true));
        q.addQuery(new NumberQuery("f", 1));
        q.addQuery(new TermQuery("f", "t"));
        q.addQuery(new PhraseQuery("f", "p1", "p2"));
        q.addQuery(new VerbatimQuery("f", "v"));

        assertEquals("(f: true OR f: 1 OR f: \"t\" OR f: \"p1 p2\" OR f: \"v\")", toString(q));
    }

    @Test
    public void testNestedQuery() {
        OrQuery q = new OrQuery(
                new NotQuery(new NumberQuery("f", 1), new NumberQuery("f", 2)),
                new AndQuery(new NumberQuery("f", 3), new NumberQuery("f", 4)),
                new OrQuery(new NumberQuery("f", 5), new NumberQuery("f", 6))
        );

        assertEquals("(NOT (f: 1 f: 2) OR (f: 3 AND f: 4) OR (f: 5 OR f: 6))", toString(q));
    }

    @Test
    public void testDeepOrQuery() {
        int i = 0;
        OrQuery q = new OrQuery(
                new OrQuery(
                        new OrQuery(
                                new NumberQuery("f", i++),
                                new NumberQuery("f", i++)
                        ),
                        new OrQuery(
                                new NumberQuery("f", i++),
                                new NumberQuery("f", i++)
                        )
                ),
                new OrQuery(
                        new OrQuery(
                                new NumberQuery("f", i++),
                                new NumberQuery("f", i++)
                        ),
                        new OrQuery(
                                new NumberQuery("f", i++),
                                new NumberQuery("f", i++)
                        )
                )
        );
        assertEquals("(((f: 0 OR f: 1) OR (f: 2 OR f: 3)) OR ((f: 4 OR f: 5) OR (f: 6 OR f: 7)))", toString(q));
    }

    @Test
    public void testDeepAndQuery() {
        int i = 0;
        AndQuery q = new AndQuery(
                new AndQuery(
                        new AndQuery(
                                new NumberQuery("f", i++),
                                new NumberQuery("f", i++)
                        ),
                        new AndQuery(
                                new NumberQuery("f", i++),
                                new NumberQuery("f", i++)
                        )
                ),
                new AndQuery(
                        new AndQuery(
                                new NumberQuery("f", i++),
                                new NumberQuery("f", i++)
                        ),
                        new AndQuery(
                                new NumberQuery("f", i++),
                                new NumberQuery("f", i++)
                        )
                )
        );
        assertEquals("(((f: 0 AND f: 1) AND (f: 2 AND f: 3)) AND ((f: 4 AND f: 5) AND (f: 6 AND f: 7)))", toString(q));
    }

    @Test
    public void testDeepNotQuery() {
        int i = 0;
        Query q = new NotQuery(
                new NotQuery(
                        new NotQuery(
                                new NumberQuery("f", i++),
                                new NumberQuery("f", i++)
                        ),
                        new NotQuery(
                                new NumberQuery("f", i++),
                                new NumberQuery("f", i++)
                        )
                ),
                new NotQuery(
                        new NotQuery(
                                new NumberQuery("f", i++),
                                new NumberQuery("f", i++)
                        ),
                        new NotQuery(
                                new NumberQuery("f", i++),
                                new NumberQuery("f", i++)
                        )
                )
        );
        assertEquals("NOT (NOT (NOT (f: 0 f: 1) NOT (f: 2 f: 3)) NOT (NOT (f: 4 f: 5) NOT (f: 6 f: 7)))", toString(q));
    }

    private static String toString(Query query) {
        return new SolrLuceneQueryVistor().visitQuery(query).toString();
    }
}
