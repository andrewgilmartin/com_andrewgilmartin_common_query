package com.andrewgilmartin.common.query.visitor;

import com.andrewgilmartin.common.query.AndQuery;
import com.andrewgilmartin.common.query.BooleanQuery;
import com.andrewgilmartin.common.query.NotQuery;
import com.andrewgilmartin.common.query.NumberQuery;
import com.andrewgilmartin.common.query.OrQuery;
import com.andrewgilmartin.common.query.PhraseQuery;
import com.andrewgilmartin.common.query.Query;
import com.andrewgilmartin.common.query.TermQuery;
import com.andrewgilmartin.common.query.VerbatimQuery;
import org.junit.Test;
import static org.junit.Assert.*;

public class SolrLuceneQueryVistorTest {

    @Test
    public void testNumber() {
        {
            Query q = new NumberQuery("f", 1);
            assertEquals("f: 1", toString(q));
        }
        {
            Query q = new NumberQuery(11, "f", 1);
            assertEquals("f: 1 ^11.0", toString(q));
        }
    }

    @Test
    public void testBoolean() {
        {
            Query q = new BooleanQuery("f", false);
            assertEquals("f: false", toString(q));
        }
        {
            Query q = new BooleanQuery(11, "f", true);
            assertEquals("f: true ^11.0", toString(q));
        }
    }

    @Test
    public void testTerm() {
        {
            Query q = new TermQuery("f", "a");
            assertEquals("f: \"a\"", toString(q));
        }
        {
            Query q = new TermQuery(11, "f", "bbb");
            assertEquals("f: \"bbb\" ^11.0", toString(q));
        }
    }

    @Test
    public void testVerbatim() {
        {
            Query q = new VerbatimQuery("f", "v");
            assertEquals("f: \"v\"", toString(q));
        }
        {
            Query q = new VerbatimQuery(11, "f", "vvv");
            assertEquals("f: \"vvv\" ^11.0", toString(q));
        }
    }

    @Test
    public void testPhrase() {
        {
            Query q = new PhraseQuery("f", "a", "b");
            assertEquals("f: \"a b\"", toString(q));
        }
        {
            Query q = new PhraseQuery(11, "f", "bbb", "ccc");
            assertEquals("f: \"bbb ccc\" ^11.0", toString(q));
        }
    }

    @Test
    public void testAnd() {
        {
            Query q = new AndQuery(1);
            assertEquals("", toString(q));
        }
        {
            Query q = new AndQuery(new NumberQuery(5, "f", 1));
            assertEquals("(f: 1 ^5.0)", toString(q));
        }
        {
            Query q = new AndQuery(3, new NumberQuery(5, "f", 1));
            assertEquals("(f: 1 ^5.0) ^3.0", toString(q));
        }
        {
            Query q = new AndQuery(new NumberQuery(5, "f", 1), new NumberQuery(10, "f", 2), new NumberQuery(15, "f", 3));
            assertEquals("(f: 1 ^5.0 AND f: 2 ^10.0 AND f: 3 ^15.0)", toString(q));
        }
        {
            Query q = new AndQuery(3, new NumberQuery(5, "f", 1), new NumberQuery(10, "f", 2), new NumberQuery(15, "f", 3));
            assertEquals("(f: 1 ^5.0 AND f: 2 ^10.0 AND f: 3 ^15.0) ^3.0", toString(q));
        }
    }

    @Test
    public void testOr() {
        {
            Query q = new OrQuery(1);
            assertEquals("", toString(q));
        }
        {
            Query q = new OrQuery(new NumberQuery(5, "f", 1));
            assertEquals("(f: 1 ^5.0)", toString(q));
        }
        {
            Query q = new OrQuery(3, new NumberQuery(5, "f", 1));
            assertEquals("(f: 1 ^5.0) ^3.0", toString(q));
        }
        {
            Query q = new OrQuery(3, new NumberQuery(5, "f", 1), new NumberQuery(10, "f", 2), new NumberQuery(15, "f", 3));
            assertEquals("(f: 1 ^5.0 OR f: 2 ^10.0 OR f: 3 ^15.0) ^3.0", toString(q));
        }
    }

    @Test
    public void testNot() {
        {
            Query q = new NotQuery(1);
            assertEquals("", toString(q));
        }
        {
            Query q = new NotQuery(3, new NumberQuery(5, "f", 1));
            assertEquals("NOT (f: 1 ^5.0) ^3.0", toString(q));
        }
        {
            Query q = new NotQuery(3, new NumberQuery(5, "f", 1), new NumberQuery(10, "f", 2), new NumberQuery(15, "f", 3));
            assertEquals("NOT (f: 1 ^5.0 f: 2 ^10.0 f: 3 ^15.0) ^3.0", toString(q));
        }
    }

    private static String toString(Query query) {
        return new SolrLuceneQueryVistor().visitQuery(query).toString();
    }

}
