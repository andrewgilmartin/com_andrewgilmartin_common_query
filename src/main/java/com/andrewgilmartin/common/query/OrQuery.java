package com.andrewgilmartin.common.query;

/**
 * At least one of the sub-queries is required.
 */
public class OrQuery extends CompoundQuery {

    public static OrQuery create(float weight, Query... queries) {
        // do we have any non-null queries?
        for (Query query : queries) {
            if (query != null) {
                // we do so let's create the boolean query
                return new OrQuery(weight, queries);
            }
        }
        return null;
    }

    public static OrQuery create(Query... queries) {
        return create(Float.NaN, queries);
    }

    public OrQuery(float weight, Query... queries) {
        super(weight);
        for (Query q : queries) {
            if (q != null) {
                addQuery(q);
            }
        }
    }

    public OrQuery(Query... queries) {
        this(Float.NaN,queries);
    }
    
    public OrQuery() {
        // empty
    }
}

// END
