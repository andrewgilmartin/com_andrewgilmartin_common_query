package com.andrewgilmartin.common.query;

import java.io.Serializable;

/**
 * Query is the base class for constructing a query tree. This tree can then be
 * manipulated to broaden or narrow the sub-queries by QueryVistor instances.
 * The sub-classes of this class parallel the Lucene query classes very closely.
 * This class and its sub-classes are only necessary because Lucene does not
 * allow visitation or walking of their query classes and so query rewriting is
 * not possible. Query rewriting is a necessary facility for intelligent
 * reshaping of human and machine generated queries.
 */
public class Query implements Serializable {

    public final static float DEFAULT_WEIGHT = Float.NaN;

    private float weight = DEFAULT_WEIGHT;

    public Query() {
        // empty
    }

    public Query(float weight) {
        this.weight = weight;
    }

    public void setWeight(float weight) {
        this.weight = weight;
    }

    public boolean hasWeight() {
        return !Float.isNaN(weight);
    }

    public float getWeight() {
        return weight;
    }
}
