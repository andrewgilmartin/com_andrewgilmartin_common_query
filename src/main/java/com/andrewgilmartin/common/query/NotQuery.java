package com.andrewgilmartin.common.query;

/**
 * All the sub-queries are prohibited.
 */
public class NotQuery extends CompoundQuery {

    public static NotQuery create(float weight, Query... queries) {
        // do we have any non-null queries?
        for (Query query : queries) {
            if (query != null) {
                // we do so let's create the boolean query
                return new NotQuery(weight, queries);
            }
        }
        return null;
    }

    public static NotQuery create(Query... queries) {
        return create(Query.DEFAULT_WEIGHT, queries);
    }

    public NotQuery(float weight, Query... queries) {
        super(weight);
        for (Query q : queries) {
            if (q != null) {
                addQuery(q);
            }
        }
    }

    public NotQuery(Query... queries) {
        this(Float.NaN, queries);
    }

    public NotQuery() {
        // empty
    }
}

// END
