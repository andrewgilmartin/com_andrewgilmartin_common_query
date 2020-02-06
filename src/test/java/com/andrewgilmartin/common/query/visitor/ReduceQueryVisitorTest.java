package com.andrewgilmartin.common.query.visitor;

import com.andrewgilmartin.common.query.AndQuery;
import com.andrewgilmartin.common.query.NotQuery;
import com.andrewgilmartin.common.query.NumberQuery;
import com.andrewgilmartin.common.query.OrQuery;
import com.andrewgilmartin.common.query.PhraseQuery;
import com.andrewgilmartin.common.query.Query;
import org.junit.Assert;
import static org.junit.Assert.assertEquals;
import org.junit.Test;

public class ReduceQueryVisitorTest {

    @Test
    public void testPhraseEmptyEliminated() {
        Query q = new PhraseQuery("f");
        
        ReduceQueryVisitor v = new ReduceQueryVisitor();
        v.setCombineWeights(true);
        Query r = v.visitQuery(q);

        Assert.assertNull(r);        
    }
    
    @Test
    public void testPhraseAllNullsEliminated() {
        Query q = new PhraseQuery("f", null, null);
        
        ReduceQueryVisitor v = new ReduceQueryVisitor();
        v.setCombineWeights(true);
        Query r = v.visitQuery(q);

        Assert.assertNull(r);        
    }
    
    @Test
    public void testPhraseNullsEliminated() {
        Query q = new PhraseQuery("f", null, "a", null, "b");
        
        ReduceQueryVisitor v = new ReduceQueryVisitor();
        v.setCombineWeights(true);
        Query r = v.visitQuery(q);

        assertEquals(PhraseQuery.class, r.getClass());
        assertEquals(2, ((PhraseQuery) r).getTerms().size());
        assertEquals("a", ((PhraseQuery) r).getTerms().get(0));
        assertEquals("b", ((PhraseQuery) r).getTerms().get(1));
    }
    
    @Test
    public void testPhraseUnchnaged() {
        Query q = new PhraseQuery("f", "a", "b");
        
        ReduceQueryVisitor v = new ReduceQueryVisitor();
        v.setCombineWeights(true);
        Query r = v.visitQuery(q);

        assertEquals(PhraseQuery.class, r.getClass());
        assertEquals(2, ((PhraseQuery) r).getTerms().size());
    }
    
    @Test
    public void testAndEliminated() {
        Query q = new AndQuery(2.0f);

        ReduceQueryVisitor v = new ReduceQueryVisitor();
        v.setCombineWeights(true);
        Query r = v.visitQuery(q);

        Assert.assertNull(r);
    }

    @Test
    public void testAndUnchanged() {
        Query q = new AndQuery(2.0f, new NumberQuery(4.0f, "f", 1), new NumberQuery(5.0f, "f", 2));

        ReduceQueryVisitor v = new ReduceQueryVisitor();
        v.setCombineWeights(true);
        Query r = v.visitQuery(q);

        assertEquals(AndQuery.class, r.getClass());
        assertEquals(2, ((AndQuery) r).getQueries().size());
    }

    @Test
    public void testAndCombineWeights() {
        Query q = new AndQuery(2.0f, new NumberQuery(4.0f, "f", 1));

        ReduceQueryVisitor v = new ReduceQueryVisitor();
        v.setCombineWeights(true);
        Query r = v.visitQuery(q);

        assertEquals(NumberQuery.class, r.getClass());
        assertEquals(8.0f, r.getWeight(), 0.0f);
    }

    @Test
    public void testAndDontCombineWeights() {
        Query q = new AndQuery(2.0f, new NumberQuery(4.0f, "f", 1));

        ReduceQueryVisitor v = new ReduceQueryVisitor();
        v.setCombineWeights(false);
        Query r = v.visitQuery(q);

        assertEquals(NumberQuery.class, r.getClass());
        assertEquals(4.0f, r.getWeight(), 0.0f);
    }

    @Test
    public void testOrEliminated() {
        Query q = new OrQuery(2.0f);

        ReduceQueryVisitor v = new ReduceQueryVisitor();
        v.setCombineWeights(true);
        Query r = v.visitQuery(q);

        Assert.assertNull(r);
    }

    @Test
    public void testOrUnchanged() {
        Query q = new OrQuery(2.0f, new NumberQuery(4.0f, "f", 1), new NumberQuery(5.0f, "f", 2));

        ReduceQueryVisitor v = new ReduceQueryVisitor();
        v.setCombineWeights(true);
        Query r = v.visitQuery(q);

        assertEquals(OrQuery.class, r.getClass());
        assertEquals(2, ((OrQuery) r).getQueries().size());
    }

    @Test
    public void testOrCombineWeights() {
        Query q = new OrQuery(2.0f, new NumberQuery(4.0f, "f", 1));

        ReduceQueryVisitor v = new ReduceQueryVisitor();
        v.setCombineWeights(true);
        Query r = v.visitQuery(q);

        assertEquals(NumberQuery.class, r.getClass());
        assertEquals(8.0f, r.getWeight(), 0.0f);
    }

    @Test
    public void testOrDontCombineWeights() {
        Query q = new OrQuery(2.0f, new NumberQuery(4.0f, "f", 1));

        ReduceQueryVisitor v = new ReduceQueryVisitor();
        v.setCombineWeights(false);
        Query r = v.visitQuery(q);

        assertEquals(NumberQuery.class, r.getClass());
        assertEquals(4.0f, r.getWeight(), 0.0f);
    }

    @Test
    public void testNotEliminated() {
        Query q = new NotQuery(2.0f);

        ReduceQueryVisitor v = new ReduceQueryVisitor();
        v.setCombineWeights(true);
        Query r = v.visitQuery(q);

        Assert.assertNull(r);
    }

    @Test
    public void testNotUnchanged() {
        Query q = new NotQuery(2.0f, new NumberQuery(4.0f, "f", 1), new NumberQuery(5.0f, "f", 2));

        ReduceQueryVisitor v = new ReduceQueryVisitor();
        v.setCombineWeights(true);
        Query r = v.visitQuery(q);

        assertEquals(NotQuery.class, r.getClass());
        assertEquals(2, ((NotQuery) r).getQueries().size());
    }
}
