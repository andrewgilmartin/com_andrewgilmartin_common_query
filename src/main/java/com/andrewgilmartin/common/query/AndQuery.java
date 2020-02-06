package com.andrewgilmartin.common.query;

/**
 * All the sub-queries are required.
 */
public class AndQuery extends CompoundQuery {

    public static AndQuery create(float weight, Query... queries) {
        // do we have any non-null queries?
        for (Query query : queries) {
            if (query != null) {
                // we do so let's create the boolean query
                return new AndQuery(weight, queries);
            }
        }
        return null;
    }

    public static AndQuery create(Query... queries) {
        return create(Float.NaN, queries);
    }

    public AndQuery(float weight, Query... queries) {
        super(weight);
        for (Query q : queries) {
            if (q != null) {
                addQuery(q);
            }
        }
    }

    public AndQuery(Query... queries) {
        this(Float.NaN,queries);
    }

    public AndQuery() {
        // empty
    }
}

// END
