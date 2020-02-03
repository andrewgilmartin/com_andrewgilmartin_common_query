package com.andrewgilmartin.common.query.visitor;

import com.andrewgilmartin.common.query.AndQuery;
import com.andrewgilmartin.common.query.NotQuery;
import com.andrewgilmartin.common.query.PhraseQuery;
import com.andrewgilmartin.common.query.Query;
import com.andrewgilmartin.common.query.TermQuery;
import java.util.Set;
import java.util.TreeSet;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class TermsGatheringVisitorTest {

    Query q;

    @Before
    public void setUp() {
        q = new AndQuery(0,
                new TermQuery("x", "a"),
                new TermQuery("y", "b"),
                new TermQuery("z", "c"),
                new NotQuery(0,
                        new TermQuery("x", "d"),
                        new TermQuery("y", "e"),
                        new NotQuery(0,
                                new TermQuery("x", "f"),
                                new TermQuery("z", "g")
                        )
                ),
                new PhraseQuery(0, "x", "h", "i"),
                new NotQuery(0,
                        new PhraseQuery(0, "y", "j", "k")
                )
        );
    }

    @After
    public void tearDown() {
    }

    @Test
    public void testNotProhibited() {
        Set<String> gatheredTerms = new TermsGatheringVisitor()
                .visit(q, new TreeSet<>());
        Assert.assertArrayEquals(new String[]{"a", "b", "c", "f", "g", "h", "i"}, gatheredTerms.toArray());
    }

    @Test
    public void testAll() {
        Set<String> gatheredTerms = new TermsGatheringVisitor().setIncludeProhibitedTerms(true).visit(q, new TreeSet<>());
        Assert.assertArrayEquals(new String[]{"a", "b", "c", "d", "e", "f", "g", "h", "i", "j", "k"}, gatheredTerms.toArray());
    }

    @Test
    public void testOneFieldIncludedNotProhibited() {
        Set<String> gatheredTerms = new TermsGatheringVisitor()
                .addIncludeField("x")
                .visit(q, new TreeSet<>());
        Assert.assertArrayEquals(new String[]{"a", "f", "h", "i"}, gatheredTerms.toArray());
    }

    @Test
    public void testOneFieldIncludedAll() {
        Set<String> gatheredTerms = new TermsGatheringVisitor()
                .addIncludeField("x")
                .setIncludeProhibitedTerms(true)
                .visit(q, new TreeSet<>());
        Assert.assertArrayEquals(new String[]{"a", "d", "f", "h", "i"}, gatheredTerms.toArray());
    }

    @Test
    public void testOneFieldExcludedNotProbibited() {
        Set<String> gatheredTerms = new TermsGatheringVisitor()
                .addExcludeField("x")
                .visit(q, new TreeSet<>());
        Assert.assertArrayEquals(new String[]{"b", "c", "g"}, gatheredTerms.toArray());
    }

    @Test
    public void testOneFieldExcludedAll() {
        Set<String> gatheredTerms = new TermsGatheringVisitor()
                .addExcludeField("x")
                .setIncludeProhibitedTerms(true)
                .visit(q, new TreeSet<>());
        Assert.assertArrayEquals(new String[]{"b", "c", "e", "g", "j", "k"}, gatheredTerms.toArray());
    }
}
