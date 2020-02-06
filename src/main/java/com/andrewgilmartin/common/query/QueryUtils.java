package com.andrewgilmartin.common.query;

import org.apache.lucene.index.Term;
import org.apache.lucene.search.BooleanClause;

public class QueryUtils {

    public static String dump(org.apache.lucene.search.Query luceneQuery) {
        return dump(luceneQuery, new StringBuilder(), "").toString();
    }

    private static StringBuilder dump(org.apache.lucene.search.Query luceneQuery, StringBuilder out, String margin) {
        String step = "  ";
        if (luceneQuery instanceof org.apache.lucene.search.TermQuery) {
            org.apache.lucene.search.TermQuery termQuery = (org.apache.lucene.search.TermQuery) luceneQuery;
            out.append(margin).append("(term ").append(termQuery.getTerm().field()).append(" ").append(termQuery.getTerm().text()).append(")\n");
        } else if (luceneQuery instanceof org.apache.lucene.search.PhraseQuery) {
            org.apache.lucene.search.PhraseQuery phraseQuery = (org.apache.lucene.search.PhraseQuery) luceneQuery;
            out.append(margin).append("(phrase ").append(phraseQuery.getField());
            for (Term term : phraseQuery.getTerms()) {
                out.append(" ").append(term.text());
            }
            out.append(")\n");
        } else if (luceneQuery instanceof org.apache.lucene.search.BoostQuery) {
            org.apache.lucene.search.BoostQuery boostQuery = (org.apache.lucene.search.BoostQuery) luceneQuery;
            out.append(margin).append("(boost ").append(Float.toString(boostQuery.getBoost())).append("\n");
            dump(boostQuery.getQuery(), out, margin + step);
            out.append(margin).append(")\n");
        } else if (luceneQuery instanceof org.apache.lucene.search.BooleanQuery) {
            org.apache.lucene.search.BooleanQuery booleanQuery = (org.apache.lucene.search.BooleanQuery) luceneQuery;
            out.append(margin).append("(boolean\n");
            for (BooleanClause clause : booleanQuery.clauses()) {
                switch (clause.getOccur()) {
                    case MUST:
                        out.append(margin).append(step).append("(must\n");
                        dump(clause.getQuery(), out, margin + step + step);
                        out.append(margin).append(step).append(")\n");
                        break;
                    case SHOULD:
                        out.append(margin).append(step).append("(should\n");
                        dump(clause.getQuery(), out, margin + step + step);
                        out.append(margin).append(step).append(")\n");
                        break;
                    case MUST_NOT:
                        out.append(margin).append(step).append("(must_not\n");
                        dump(clause.getQuery(), out, margin + step + step);
                        out.append(margin).append(step).append(")\n");
                        break;
                }
            }
            out.append(margin).append(")\n");
        }
        return out;
    }
}
