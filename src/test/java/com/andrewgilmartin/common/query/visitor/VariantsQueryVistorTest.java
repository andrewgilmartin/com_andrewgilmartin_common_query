package com.andrewgilmartin.common.query.visitor;

import com.andrewgilmartin.common.query.OrQuery;
import com.andrewgilmartin.common.query.Query;
import com.andrewgilmartin.common.query.TermQuery;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import org.junit.Test;

public class VariantsQueryVistorTest {

    @Test
    public void testSimple() {
        TermQuery in = new TermQuery("f", "a");
        Map<String, List<String>> data = new HashMap<>();
        data.put("a", Arrays.asList("x", "y", "z"));
        VariantsQueryVistor vistor = new VariantsQueryVistor(0.0001f, data);

        OrQuery out = (OrQuery) vistor.visitQuery(in);
        assertNotNull(out);
        assertEquals(4, out.getQueries().size());
        assertEquals("a", ((TermQuery) out.getQueries().get(0)).getTerm());
        assertEquals("x", ((TermQuery) out.getQueries().get(1)).getTerm());
        assertEquals("y", ((TermQuery) out.getQueries().get(2)).getTerm());
        assertEquals("z", ((TermQuery) out.getQueries().get(3)).getTerm());
    }

}
