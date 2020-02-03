package com.andrewgilmartin.common.query.visitor;

import java.util.Collection;
import java.util.Iterator;
import com.andrewgilmartin.common.query.AndQuery;
import com.andrewgilmartin.common.query.BooleanQuery;
import com.andrewgilmartin.common.query.LuceneQuery;
import com.andrewgilmartin.common.query.NotQuery;
import com.andrewgilmartin.common.query.NumberQuery;
import com.andrewgilmartin.common.query.OrQuery;
import com.andrewgilmartin.common.query.PhraseQuery;
import com.andrewgilmartin.common.query.Query;
import com.andrewgilmartin.common.query.QueryException;
import com.andrewgilmartin.common.query.TermQuery;
import com.andrewgilmartin.common.query.VerbatimQuery;

public class SolrLuceneQueryVistor extends QueryVisitor<StringBuilder> {

    public String getDefType() {
        return "lucene";
    }

    @Override
    public StringBuilder visitQuery(Query query) throws QueryException {
        return visit(query, new StringBuilder());
    }

    public StringBuilder visitQuery(Query query, StringBuilder builder) throws QueryException {
        return visit(query, builder);
    }

    @Override
    protected StringBuilder visit(TermQuery query, StringBuilder builder) throws QueryException {
        if (builder.length() > 0) {
            builder.append(' ');
        }
        builder.append(query.getField()).append(": ");
        quote(query.getTerm(), builder);
        return builder;
    }

    @Override
    protected StringBuilder visit(NumberQuery query, StringBuilder builder) throws QueryException {
        if (builder.length() > 0) {
            builder.append(' ');
        }
        builder.append(query.getField()).append(": ").append(query.getNumber().toString());
        return builder;
    }

    @Override
    protected StringBuilder visit(VerbatimQuery query, StringBuilder builder) throws QueryException {
        if (builder.length() > 0) {
            builder.append(' ');
        }
        builder.append(query.getField()).append(": ");
        quote(query.getTerm(), builder);
        if (query.hasWeight()) {
            builder.append(" ^").append(query.getWeight());
        }
        return builder;
    }

    @Override
    protected StringBuilder visit(PhraseQuery query, StringBuilder builder) throws QueryException {
        if (builder.length() > 0) {
            builder.append(' ');
        }
        builder.append(query.getField()).append(": ");
        quote(query.getTerms(), builder);
        if (query.hasWeight()) {
            builder.append(" ^").append(query.getWeight());
        }
        return builder;
    }

    @Override
    protected StringBuilder visit(BooleanQuery query, StringBuilder builder) throws QueryException {
        if (builder.length() > 0) {
            builder.append(' ');
        }
        builder.append(query.getField()).append(": ").append(Boolean.toString(query.getBoolean()));
        return builder;
    }

    @Override
    protected StringBuilder visit(AndQuery query, StringBuilder builder) throws QueryException {
        switch (query.getQueries().size()) {
            case 0:
                break;
            case 1:
                visit(query.getQueries().get(0), builder);
                break;
            default:
                builder.append("(");
                Iterator<Query> i = query.getQueries().iterator();
                visit(i.next(), builder);
                while (i.hasNext()) {
                    builder.append(" AND ");
                    visit(i.next(), builder);
                }
                builder.append(")");
                break;
        }
        if (query.hasWeight()) {
            builder.append(" ^").append(query.getWeight());
        }
        return builder;
    }

    @Override
    protected StringBuilder visit(OrQuery query, StringBuilder builder) throws QueryException {
        switch (query.getQueries().size()) {
            case 0:
                break;
            case 1:
                visit(query.getQueries().get(0), builder);
                break;
            default:
                builder.append("(");
                Iterator<Query> i = query.getQueries().iterator();
                visit(i.next(), builder);
                while (i.hasNext()) {
                    builder.append(" OR ");
                    visit(i.next(), builder);
                }
                builder.append(")");
                break;
        }
        if (query.hasWeight()) {
            builder.append(" ^").append(query.getWeight());
        }
        return builder;
    }

    @Override
    protected StringBuilder visit(NotQuery query, StringBuilder builder) throws QueryException {
        switch (query.getQueries().size()) {
            case 0:
                break;
            case 1:
                visit(query.getQueries().get(0), builder);
                break;
            default:
                builder.append("(");
                Iterator<Query> i = query.getQueries().iterator();
                visit(i.next(), builder);
                while (i.hasNext()) {
                    builder.append(" NOT ");
                    visit(i.next(), builder);
                }
                builder.append(")");
                break;
        }
        if (query.hasWeight()) {
            builder.append(" ^").append(query.getWeight());
        }
        return builder;
    }

    @Override
    protected StringBuilder visit(LuceneQuery query, StringBuilder builder) throws QueryException {
        throw new QueryException("can't convert lucene queries");
    }

    protected StringBuilder quote(Collection<String> values, StringBuilder builder) {
        builder.append('"');
        boolean next = false;
        for (String value : values) {
            if (next) {
                builder.append(' ');
            }
            int l = value.length();
            for (int i = 0; i < l; i++) {
                char c = value.charAt(i);
                if (c <= '~') {
                    builder.append(ESCAPED_ASCII[c]);
                } else {
                    builder.append("\\u");
                    builder.append(HEX_DIGITS[(c & 0xF000) >>> 12]);
                    builder.append(HEX_DIGITS[(c & 0x0F00) >>> 8]);
                    builder.append(HEX_DIGITS[(c & 0x00F0) >>> 4]);
                    builder.append(HEX_DIGITS[(c & 0x000F)]);
                }
            }
        }
        builder.append('"');
        return builder;
    }

    protected StringBuilder quote(String value, StringBuilder builder) {
        builder.append('"');
        int l = value.length();
        for (int i = 0; i < l; i++) {
            char c = value.charAt(i);
            if (c <= '~') {
                builder.append(ESCAPED_ASCII[c]);
            } else {
                builder.append("\\u");
                builder.append(HEX_DIGITS[(c & 0xF000) >>> 12]);
                builder.append(HEX_DIGITS[(c & 0x0F00) >>> 8]);
                builder.append(HEX_DIGITS[(c & 0x00F0) >>> 4]);
                builder.append(HEX_DIGITS[(c & 0x000F)]);
            }
        }
        builder.append('"');
        return builder;
    }
    
    private final static char[] HEX_DIGITS = new char[]{
        '0',
        '1',
        '2',
        '3',
        '4',
        '5',
        '6',
        '7',
        '8',
        '9',
        'a',
        'b',
        'c',
        'd',
        'e',
        'f'
    };
    
    private final static String[] ESCAPED_ASCII = new String[]{
        "\\u0000",
        "\\u0001",
        "\\u0002",
        "\\u0003",
        "\\u0004",
        "\\u0005",
        "\\u0006",
        "\\u0007",
        "\\b",
        "\\t",
        "\\n",
        "\\u000b",
        "\\f",
        "\\r",
        "\\u000e",
        "\\u000f",
        "\\u0010",
        "\\u0011",
        "\\u0012",
        "\\u0013",
        "\\u0014",
        "\\u0015",
        "\\u0016",
        "\\u0017",
        "\\u0018",
        "\\u0019",
        "\\u001a",
        "\\u001b",
        "\\u001c",
        "\\u001d",
        "\\u001e",
        "\\u001f",
        " ",
        "!",
        "\\\"",
        "#",
        "$",
        "%",
        "&",
        "\\'", // CYA
        "(",
        ")",
        "*",
        "+",
        ",",
        "-",
        ".",
        "\\/",
        "0",
        "1",
        "2",
        "3",
        "4",
        "5",
        "6",
        "7",
        "8",
        "9",
        "\\:", // CYA
        ";",
        "<",
        "=",
        ">",
        "?",
        "@",
        "A",
        "B",
        "C",
        "D",
        "E",
        "F",
        "G",
        "H",
        "I",
        "J",
        "K",
        "L",
        "M",
        "N",
        "O",
        "P",
        "Q",
        "R",
        "S",
        "T",
        "U",
        "V",
        "W",
        "X",
        "Y",
        "Z",
        "[",
        "\\\\",
        "]",
        "^",
        "_",
        "`",
        "a",
        "b",
        "c",
        "d",
        "e",
        "f",
        "g",
        "h",
        "i",
        "j",
        "k",
        "l",
        "m",
        "n",
        "o",
        "p",
        "q",
        "r",
        "s",
        "t",
        "u",
        "v",
        "w",
        "x",
        "y",
        "z",
        "{",
        "|",
        "}",
        "~",
        "\\u007f"
    };
}

// END
