package com.andrewgilmartin.common.query.visitor;

import java.util.Iterator;
import com.andrewgilmartin.common.query.AndQuery;
import com.andrewgilmartin.common.query.BooleanQuery;
import com.andrewgilmartin.common.query.LuceneQuery;
import com.andrewgilmartin.common.query.NotQuery;
import com.andrewgilmartin.common.query.NumberQuery;
import com.andrewgilmartin.common.query.OrQuery;
import com.andrewgilmartin.common.query.PhraseQuery;
import com.andrewgilmartin.common.query.Query;
import com.andrewgilmartin.common.query.TermQuery;
import com.andrewgilmartin.common.query.VerbatimQuery;

public class DebugQueryVisitor extends QueryVisitor {

    private int nesting = 0;

    @Override
    public String visitQuery(Query query) {
        return visit(query, new StringBuilder()).toString();
    }

    @Override
    protected Object visit(TermQuery query, Object data) {
        append("(" + query.getClass().getName(), data);
        nesting++;
        append("(weight " + query.getWeight() + ")", data);
        append("(field " + query.getField() + ")", data);
        append("(term " + query.getTerm() + ")", data);
        nesting--;
        append(")", data);
        return data;
    }

    @Override
    protected Object visit(VerbatimQuery query, Object data) {
        append("(" + query.getClass().getName(), data);
        nesting++;
        append("(weight " + query.getWeight() + ")", data);
        append("(field " + query.getField() + ")", data);
        append("(term " + query.getTerm() + ")", data);
        nesting--;
        append(")", data);
        return data;
    }

    @Override
    protected Object visit(PhraseQuery query, Object data) {
        append("(" + query.getClass().getName(), data);
        nesting++;
        append("(weight " + query.getWeight() + ")", data);
        append("(field " + query.getField() + ")", data);
        Iterator<String> terms = query.getTerms().iterator();
        for (int i = 0; terms.hasNext(); i++) {
            append("// " + (i + 1), data);
            append("(term " + terms.next() + ")", data);
        }
        nesting--;
        append(")", data);
        return data;
    }

    @Override
    protected Object visit(NumberQuery query, Object data) {
        append("(" + query.getClass().getName(), data);
        nesting++;
        append("(weight " + query.getWeight() + ")", data);
        append("(field " + query.getField() + ")", data);
        append("(number " + query.getNumber() + ")", data);
        nesting--;
        append(")", data);
        return data;
    }

    @Override
    protected Object visit(BooleanQuery query, Object data) {
        append("(" + query.getClass().getName(), data);
        nesting++;
        append("(weight " + query.getWeight() + ")", data);
        append("(field " + query.getField() + ")", data);
        append("(boolean " + query.getBoolean() + ")", data);
        nesting--;
        append(")", data);
        return data;
    }

    @Override
    protected Object visit(LuceneQuery query, Object data) {
        StringBuilder buffer = (StringBuilder) data;
        append("(" + query.getClass().getName(), data);
        buffer.append(query.getLuceneQuery().toString());
        append(")", data);
        return data;
    }

    @Override
    protected Object visit(AndQuery query, Object data) {
        append("(and " + query.getClass().getName(), data);
        nesting++;
        append("(weight " + query.getWeight() + ")", data);
        Iterator<Query> queries = query.getQueries().iterator();
        for (int i = 0; queries.hasNext(); i++) {
            append("// " + (i + 1), data);
            visit(queries.next(), data);
        }
        nesting--;
        append(")", data);
        return data;
    }

    @Override
    protected Object visit(OrQuery query, Object data) {
        append("(or " + query.getClass().getName(), data);
        nesting++;
        append("(weight " + query.getWeight() + ")", data);
        Iterator<Query> queries = query.getQueries().iterator();
        for (int i = 0; queries.hasNext(); i++) {
            append("// " + (i + 1), data);
            visit(queries.next(), data);
        }
        nesting--;
        append(")", data);
        return data;
    }

    @Override
    protected Object visit(NotQuery query, Object data) {
        append("(not " + query.getClass().getName(), data);
        nesting++;
        append("(weight " + query.getWeight() + ")", data);
        Iterator<Query> queries = query.getQueries().iterator();
        for (int i = 0; queries.hasNext(); i++) {
            append("// " + (i + 1), data);
            visit(queries.next(), data);
        }
        nesting--;
        append(")", data);
        return data;
    }

    private void append(String text, Object data) {
        StringBuilder buffer = (StringBuilder) data;
        for (int i = 0; i < nesting; i++) {
            buffer.append("    ");
        }
        for (int i = 0; i < text.length(); i++) {
            char c = text.charAt(i);
            if (' ' <= c && c <= '~') {
                buffer.append(c);
            } else {
                buffer.append('\\');
                buffer.append('u');
                buffer.append(HEX_DIGITS[(c & 0xF000) >>> 12]);
                buffer.append(HEX_DIGITS[(c & 0x0F00) >>> 8]);
                buffer.append(HEX_DIGITS[(c & 0x00F0) >>> 4]);
                buffer.append(HEX_DIGITS[(c & 0x000F)]);
            }
        }
        buffer.append("\n");
    }

    private final static char[] HEX_DIGITS = new char[]{'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'};
}
