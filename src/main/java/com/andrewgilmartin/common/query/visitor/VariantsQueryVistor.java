package com.andrewgilmartin.common.query.visitor;

import com.andrewgilmartin.common.query.OrQuery;
import com.andrewgilmartin.common.query.Query;
import com.andrewgilmartin.common.query.TermQuery;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Simple visitor to augment the use of one term with its variants. Ie, if the
 * term is "a" and the variants are "x", "y" and "z" then the query becomes
 *
 * (a OR x^vw OR y^vw OR z^vw )^qw
 *
 * Where qw is the original query weight and vw is the variant weight.
 *
 * Only terms in TermQuery are augmented.
 */
public class VariantsQueryVistor extends QueryVisitorAdaptor<Void> {

    private final float variantWeight;
    private final Map<String, List<String>> variants = new HashMap<>();

    public VariantsQueryVistor(float variantWeight, Map<String, List<String>> variants) {
        this.variantWeight = variantWeight;
        this.variants.putAll(variants);
    }

    @Override
    protected Query visit(TermQuery query, Void data) {
        if (variants.containsKey(query.getTerm())) {
            OrQuery variantsQuery = new OrQuery(query.getWeight());
            variantsQuery.addQuery(new TermQuery(query.getField(), query.getTerm()));
            for (String variantTerm : variants.get(query.getTerm())) {
                variantsQuery.addQuery(new TermQuery(variantWeight, query.getField(), variantTerm));
            }
            return variantsQuery;
        }
        return query;
    }

}
