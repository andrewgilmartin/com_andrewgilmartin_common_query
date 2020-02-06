package com.andrewgilmartin.common.query.visitor;

import java.util.regex.Pattern;
import com.andrewgilmartin.common.query.PhraseQuery;
import com.andrewgilmartin.common.query.Query;
import com.andrewgilmartin.common.query.TermQuery;

/**
 * Applies the same term transformation as Lucene's StandardAnalyzer. This is
 * lowercasing, removing possessives, and removing acronym dots.
 */
public class StandardAnalyzerQueryVisitor extends QueryVisitorAdaptor<Void> {

    private static final String UNICODE_LETTER_PATTERN
            = // Note that the correct pattern is "\p{L}" but this does not consistently compile. This is a workaround. AJG
            "["
            + "\u0041-\u005a"
            + "\u0061-\u007a"
            + "\u00c0-\u00d6"
            + "\u00d8-\u00f6"
            + "\u00f8-\u00ff"
            + "\u0100-\u1fff"
            + "\u3040-\u318f"
            + "\u3300-\u337f"
            + "\u3400-\u3d2d"
            + "\u4e00-\u9fff"
            + "\uf900-\ufaff"
            + "]";

    // The acronym pattern is two or more occurance of a Unicode letter and
    // dot pairs. This pattern matches the one in Bongo's StandardTokenizer (JavaCC source).
    private Pattern ACRONYM_PATTERN = Pattern.compile("^" + UNICODE_LETTER_PATTERN + "\\.(?:" + UNICODE_LETTER_PATTERN + "\\.)+$");

    public Query visit(Query query) {
        return visit(query, null);
    }

    @Override
    protected Query visit(TermQuery query, Void data) {
        TermQuery tq = new TermQuery(query.getWeight(), query.getField(), filter(query.getTerm()));
        return tq;
    }

    @Override
    protected Query visit(PhraseQuery query, Void data) {
        PhraseQuery pq = new PhraseQuery(query.getWeight(), query.getField());
        for (String term : query.getTerms()) {
            pq.addTerm(filter(term));
        }
        return pq;
    }

    protected String filter(String term) {
        // lowercase
        term = term.toLowerCase();
        // remove possesive
        if (term.endsWith("'s")) {
            term = term.substring(0, term.length() - 2);
        }
        // remove acronym dots
        if (ACRONYM_PATTERN.matcher(term).matches()) {
            StringBuilder t = new StringBuilder(term.length() / 2);
            for (int i = 0; i < term.length(); i += 2 /* step over letter-dot character pairs */) {
                t.append(term.charAt(i));
            }
            term = t.toString();
        }
        // done
        return term;
    }
}

// END
